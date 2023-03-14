package com.bigkel.market.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.bigkel.market.seckill.feign.CouponFeignService;
import com.bigkel.market.seckill.feign.ProductFeignService;
import com.bigkel.market.seckill.interceptor.LoginUserInterceptor;
import com.bigkel.market.seckill.service.SeckillService;
import com.bigkel.market.seckill.to.SeckillSkuRedisTo;
import com.bigkel.market.seckill.vo.SeckillSessionWithSkusVo;
import com.bigkel.market.seckill.vo.SkuInfoVo;
import com.itchenyang.common.to.mq.SeckillOrderTo;
import com.itchenyang.common.utils.R;
import com.itchenyang.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private CouponFeignService couponFeignService;

    @Resource
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private final String SECKILL_CHARE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {

        //1、扫描最近三天的商品需要参加秒杀的活动
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            //上架商品
            List<SeckillSessionWithSkusVo> sessionData = lates3DaySession.getData("data", new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            if (sessionData != null && sessionData.size() > 0) {
                //缓存到Redis
                //1、缓存活动信息
                saveSessionInfos(sessionData);

                //2、缓存活动的关联商品信息
                saveSessionSkuInfo(sessionData);
            }
        }
    }

    /**
     * 缓存秒杀活动信息
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusVo> sessions) {

        sessions.forEach(session -> {

            //获取当前活动的开始和结束时间的时间戳
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();

            //存入到Redis中的key
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;

            //判断Redis中是否有该信息，如果没有才进行添加
            Boolean hasKey = redisTemplate.hasKey(key);
            //缓存活动信息
            if (hasKey != null && !hasKey) {
                //获取到活动中所有商品的skuId
                List<String> skuIds = session.getSkuRelationEntities().stream()
                        .map(item -> item.getPromotionSessionId() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,skuIds);
            }
        });

    }

    /**
     * 缓存秒杀活动所关联的商品信息
     * @param sessions
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> sessions) {

        sessions.forEach(session -> {
            //准备hash操作，绑定hash
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            session.getSkuRelationEntities().forEach(seckillSkuVo -> {
                //生成随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString();
                Boolean hasKey = operations.hasKey(redisKey);
                if (hasKey != null && !hasKey) {

                    //缓存我们商品信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    //1、先查询sku的基本信息，调用远程服务
                    R info = productFeignService.getSkuInfo(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo",new TypeReference<SkuInfoVo>(){});
                        redisTo.setSkuInfo(skuInfo);
                    }

                    //2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);

                    //3、设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    //4、设置商品的随机码（防止恶意攻击）
                    redisTo.setRandomCode(token);

                    //序列化json格式存入Redis中
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(redisKey,seckillValue);

                    //如果当前这个场次的商品库存信息已经上架就不需要上架
                    //5、使用库存作为分布式Redisson信号量（限流）
                    // 使用库存作为分布式信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }


    /**
     * 获取到当前可以参加秒杀商品的信息
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        long curTime = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                // seckill:sessions:1594396764000_1594453242000
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                //获取存入Redis商品的开始时间
                long startTime = Long.parseLong(s[0]);
                //获取存入Redis商品的结束时间
                long endTime = Long.parseLong(s[1]);
                if (curTime >= startTime && curTime <= endTime) {
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    assert range != null;
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
                    List<String> listValue = hashOps.multiGet(range);
                    if (listValue != null && listValue.size() > 0) {
                        return listValue.stream()
                                .map(item -> JSON.parseObject(item, SeckillSkuRedisTo.class))
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillNotice(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String regex = "\\d-" + skuId;
                if (Pattern.matches(regex, key)) {
                    String s = hashOps.get(key);
                    SeckillSkuRedisTo redisTo = JSONObject.parseObject(s, SeckillSkuRedisTo.class);
                    long current = System.currentTimeMillis();
                    if (current < redisTo.getStartTime() || current > redisTo.getEndTime()) {
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        ThreadLocal<MemberRespVo> loginUser = LoginUserInterceptor.loginUser;

        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String json = hashOps.get(killId);
        if (!StringUtils.isEmpty(json)) {
            SeckillSkuRedisTo to = JSONObject.parseObject(json, SeckillSkuRedisTo.class);
            long curTime = new Date().getTime();
            Long startTime = to.getStartTime();
            Long endTime = to.getEndTime();
            // 1、验证时间
            if (curTime >= startTime && curTime <= endTime) {
                // 2、验证检验码，sessionId-skuId
                String randomCode = to.getRandomCode();
                String id = to.getPromotionSessionId() + "-" + to.getSkuId();
                if (id.equals(killId) && randomCode.equals(key)) {
                    // 3、验证购物数量
                    Integer limit = to.getSeckillLimit();
                    if (num <= limit) {
                        // 4、验证当前用户是否已经购买
                        MemberRespVo vo = loginUser.get();
                        String redisKey = vo.getId() + "-" + id;
                        Boolean buyed = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), endTime - curTime, TimeUnit.MILLISECONDS);
                        if (!buyed) {
                            RSemaphore semaphore = redissonClient.getSemaphore(key);
                            try {
                                boolean b = semaphore.tryAcquire(num, 20, TimeUnit.MILLISECONDS);
                                if (b) {
                                    // 秒杀成功，发送消息
                                    String timeId = IdWorker.getTimeId();
                                    SeckillOrderTo orderTo = new SeckillOrderTo();
                                    orderTo.setOrderSn(timeId);
                                    orderTo.setMemberId(vo.getId());
                                    orderTo.setNum(num);
                                    orderTo.setPromotionSessionId(to.getPromotionSessionId());
                                    orderTo.setSkuId(to.getSkuId());
                                    orderTo.setSeckillPrice(to.getSeckillPrice());
                                    rabbitTemplate.convertAndSend("order-event-exchange, order.seckill.order", orderTo);
                                    return timeId;
                                }
                                return null;
                            } catch (InterruptedException e) {
                                return null;
                            }
                        }
                    }
                }
            }

        }
        return null;
    }
}
