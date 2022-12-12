package com.itchenyang.market.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itchenyang.common.constant.CartConstant;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.cart.feign.ProductFeignService;
import com.itchenyang.market.cart.interceptor.CartInterceptor;
import com.itchenyang.market.cart.service.CartService;
import com.itchenyang.market.cart.vo.CartItemVo;
import com.itchenyang.market.cart.vo.CartVo;
import com.itchenyang.market.cart.vo.SkuInfoVo;
import com.itchenyang.market.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author BigKel
 * @createTime 2022/11/18
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * 加入购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        // 添加商品信息
        String info = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(info)) {
            CartItemVo cartItem = new CartItemVo();
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setSkuId(skuInfo.getSkuId());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setCount(num);
            }, executor);

            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();

            String cartItemJson = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), cartItemJson);
            return cartItem;
        } else {
            CartItemVo cartItem = JSON.parseObject(info, CartItemVo.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    /**
     * 根据skuId获取购物项
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo getCartBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        // 添加商品信息
        String info = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(info, CartItemVo.class);
    }

    /**
     * 获取购物车列表
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CartVo getCarts() throws ExecutionException, InterruptedException {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        CartVo cart = new CartVo();
        if (userInfo.getUserId() != null) {
            // 用户已登录
            String tempCartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
            List<CartItemVo> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null && tempCartItems.size() > 0) {
                for (CartItemVo cartItem : tempCartItems) {
                    // 将临时购物车的内容加入到登录用户的购物车中
                    // 若已登录，addToCart会拿到登录用户的缓存，将数据合并到缓存中
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
            }

            // 清空临时用户缓存
            clearTempCart(tempCartKey);

            String cartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            // 临时用户
            String cartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 清空临时购物车
     * @param tempCartKey
     */
    @Override
    public void clearTempCart(String tempCartKey) {
        redisTemplate.delete(tempCartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        CartItemVo item = getCartBySkuId(skuId);
        item.setCheck(checked == 1);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(item));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        CartItemVo item = getCartBySkuId(skuId);
        item.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(item));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> cartItems = Objects.requireNonNull(getCartItems(cartKey))
                    .stream()
                    .filter(CartItemVo::getCheck)
                    .peek(item -> {
                        // 重新设置价格
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                    }).filter(item -> item.getPrice() != null)
                    .collect(Collectors.toList());
            return cartItems;
        }
    }

    /**
     * 操作哪个购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfo.getUserId() != null) {
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }
}
