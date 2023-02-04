package com.itchenyang.market.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.exception.NoStockException;
import com.itchenyang.common.to.OrderTo;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.common.vo.MemberRespVo;
import com.itchenyang.market.order.constant.OrderConstant;
import com.itchenyang.market.order.dao.OrderDao;
import com.itchenyang.market.order.entity.OrderEntity;
import com.itchenyang.market.order.entity.OrderItemEntity;
import com.itchenyang.market.order.enume.OrderStatusEnum;
import com.itchenyang.market.order.feign.CartFeignService;
import com.itchenyang.market.order.feign.MemberFeignService;
import com.itchenyang.market.order.feign.ProductFeignService;
import com.itchenyang.market.order.feign.WareFeignService;
import com.itchenyang.market.order.interceptor.LoginIntercepter;
import com.itchenyang.market.order.service.OrderItemService;
import com.itchenyang.market.order.service.OrderService;
import com.itchenyang.market.order.to.OrderCreateTo;
import com.itchenyang.market.order.to.SpuInfoVo;
import com.itchenyang.market.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirm = new OrderConfirmVo();

        MemberRespVo memberRespVo = LoginIntercepter.threadLocal.get();

        // 异步问题，导致副线程拿不到主线程的threadlocal的数据，即存放在threadlocal的相关请求数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 获取用户地址信息
        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getCurrentUserAddress(memberRespVo.getId());
            orderConfirm.setMemberAddressVos(address);
        }, executor);

        // 获取用户购物车信息
        CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
            orderConfirm.setItems(cartItems);
        }, executor).thenRun(() -> {
            List<Long> skuIds = orderConfirm.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            List<SkuHasStockTo> hasStock = wareFeignService.skuHasStock(skuIds);
            if (hasStock != null) {
                Map<Long, Boolean> map = hasStock.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getStock));
                orderConfirm.setStocks(map);
            }
        });

        orderConfirm.setIntegration(memberRespVo.getIntegration());

        // token防刷
        String replace = UUID.randomUUID().toString().replace("-", "");
        orderConfirm.setOrderToken(replace);
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), replace, 30, TimeUnit.MINUTES);

        CompletableFuture.allOf(getAddress, getCartItems).get();
        return orderConfirm;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo orderSubmit(OrderSubmitVo vo){
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();

        MemberRespVo memberRespVo = LoginIntercepter.threadLocal.get();
        // 验证令牌，对比令牌和删除令牌必须保证其原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) return 0 else return 1 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),
                vo.getOrderToken());

        if (execute == 0L) {
            // 验证通过
            // 下单：验价，锁库存，创建订单
            // 1、创建订单，订单项信息
            OrderCreateTo orderCreateTo = createOrder();
            // 2、验价
            BigDecimal payAmount = orderCreateTo.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();  // 前端提交的价格
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                // 保存订单
                saveOrder(orderCreateTo);
                // 库存锁定，有异常就回滚
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(orderCreateTo.getOrder().getOrderSn());

                List<OrderItemVo> locks = orderCreateTo.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);

                R r = wareFeignService.lockOrderStock(lockVo);
                if (r.getCode() == 0) {
//                    int i = 10 / 0;
                    // 订单创建成功 发送给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderCreateTo.getOrder());

                    responseVo.setCode(0);
                    responseVo.setOrder(orderCreateTo.getOrder());
                } else {
                    responseVo.setCode(3);  // 没有库存
                    throw new NoStockException(r.getOrDefault("msg", "没有库存，锁定失败").toString());
                }
            } else {
                responseVo.setCode(2);  // 校验价格不通过
            }
        } else {
            responseVo.setCode(1);  // 令牌验证不通过
        }
        return responseVo;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity entity = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));

        return entity;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        // 查询订单状态
        OrderEntity curOrder = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", entity.getOrderSn()));
        if (curOrder.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            // 关单
            OrderEntity update = new OrderEntity();
            update.setId(curOrder.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            baseMapper.updateById(update);

            // 发给MQ，用于库存解锁
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(curOrder, orderTo);
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
        }
    }

    /**
     * 保存订单以及订单项
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        this.save(order);

        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder() {

        OrderCreateTo createTo = new OrderCreateTo();

        //1、生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = builderOrder(orderSn);

        //2、获取到所有的订单项
        List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);

        //3、验价(计算价格、积分等信息)
        computePrice(orderEntity,orderItemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(orderItemEntities);

        return createTo;
    }

    /**
     * 计算价格的方法
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        //总价
        BigDecimal total = new BigDecimal("0.0");
        //优惠价
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        //积分、成长值
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        //订单总额，叠加每一个订单项的总额信息
        for (OrderItemEntity orderItem : orderItemEntities) {
            //优惠价格信息
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            intergration = intergration.add(orderItem.getIntegrationAmount());

            //总价
            total = total.add(orderItem.getRealAmount());

            //积分信息和成长值信息
            integrationTotal += orderItem.getGiftIntegration();
            growthTotal += orderItem.getGiftGrowth();

        }
        //1、订单价格相关的
        orderEntity.setTotalAmount(total);
        //设置应付总额(总额+运费)
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //设置积分成长值信息
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);

        //设置删除状态(0-未删除，1-已删除)
        orderEntity.setDeleteStatus(0);

    }

    /**
     * 构建订单数据
     * @param orderSn
     * @return
     */
    private OrderEntity builderOrder(String orderSn) {

        //获取当前用户登录信息
        MemberRespVo memberResponseVo = LoginIntercepter.threadLocal.get();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberUsername(memberResponseVo.getUsername());

        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();

        //远程获取收货地址和运费信息
        R fareAddressVo = wareFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = fareAddressVo.getData("fare", new TypeReference<FareVo>() {});

        //获取到运费信息
        BigDecimal fare = fareResp.getFare();
        orderEntity.setFreightAmount(fare);

        //获取到收货地址信息
        MemberAddressVo address = fareResp.getAddress();
        //设置收货人信息
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());

        //设置订单相关的状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setConfirmStatus(0);
        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    public List<OrderItemEntity> builderOrderItems(String orderSn) {

        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();

        //最后确定每个购物项的价格
        List<OrderItemVo> currentCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            orderItemEntityList = currentCartItems.stream().map((items) -> {
                //构建订单项数据
                OrderItemEntity orderItemEntity = builderOrderItem(items);
                orderItemEntity.setOrderSn(orderSn);

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return orderItemEntityList;
    }

    /**
     * 构建某一个订单项的数据
     * @param items
     * @return
     */
    private OrderItemEntity builderOrderItem(OrderItemVo items) {

        OrderItemEntity orderItemEntity = new OrderItemEntity();

        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //获取spu的信息
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoData = spuInfo.getData("spuInfo", new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandName());
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());

        //2、商品的sku信息
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(items.getTitle());
        orderItemEntity.setSkuPic(items.getImage());
        orderItemEntity.setSkuPrice(items.getPrice());
        orderItemEntity.setSkuQuantity(items.getCount());

        //使用StringUtils.collectionToDelimitedString将list集合转换为String
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttrValues(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);

        //3、商品的优惠信息

        //4、商品的积分信息
        orderItemEntity.setGiftGrowth(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());
        orderItemEntity.setGiftIntegration(items.getPrice().multiply(new BigDecimal(items.getCount())).intValue());

        //5、订单项的价格信息
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);

        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);

        return orderItemEntity;
    }
}