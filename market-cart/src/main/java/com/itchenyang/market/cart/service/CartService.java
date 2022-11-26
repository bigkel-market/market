package com.itchenyang.market.cart.service;

import com.itchenyang.market.cart.vo.CartItemVo;
import com.itchenyang.market.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

/**
 * @author BigKel
 * @createTime 2022/11/18
 */
public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartBySkuId(Long skuId);

    CartVo getCarts() throws ExecutionException, InterruptedException;

    void clearTempCart(String tempCartKey);

    void checkItem(Long skuId, Integer checked);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
