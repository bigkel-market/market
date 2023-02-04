package com.itchenyang.market.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.exception.NoStockException;
import com.itchenyang.common.to.OrderTo;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.to.mq.StockDetailTo;
import com.itchenyang.common.to.mq.StockLockedTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.ware.dao.WareSkuDao;
import com.itchenyang.market.ware.entity.WareOrderTaskDetailEntity;
import com.itchenyang.market.ware.entity.WareOrderTaskEntity;
import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.itchenyang.market.ware.feign.OrderFeignService;
import com.itchenyang.market.ware.service.WareOrderTaskDetailService;
import com.itchenyang.market.ware.service.WareOrderTaskService;
import com.itchenyang.market.ware.service.WareSkuService;
import com.itchenyang.market.ware.vo.OrderItemVo;
import com.itchenyang.market.ware.vo.OrderVo;
import com.itchenyang.market.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private OrderFeignService orderFeignService;

    public void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId) {
        wareSkuDao.unLockStock(skuId, wareId, num);

        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(taskDetailId);
        detailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(detailEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockTo> hasStock(List<Long> skuIds) {
        List<SkuHasStockTo> list = skuIds.stream().map(skuId -> {
            SkuHasStockTo hasStock = new SkuHasStockTo();
            Integer stock = baseMapper.getStockBySkuId(skuId);
            hasStock.setSkuId(skuId);
            hasStock.setStock(stock != null && stock > 0);
            return hasStock;
        }).collect(Collectors.toList());
        return list;
    }

    @Transactional
    @Override
    public Boolean lockOrderStock(WareSkuLockVo lockVo) {
        /**
         * 保存库存工作单，追溯
         */
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity();
        orderTaskEntity.setOrderSn(lockVo.getOrderSn());
        wareOrderTaskService.save(orderTaskEntity);

        // 找到商品在哪个仓库有库存
        List<OrderItemVo> locks = lockVo.getLocks();
        List<SkuWareHasStock> collects = locks.stream().map(lock -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(lock.getSkuId());
            stock.setCount(lock.getCount());

            List<Long> wareIds = baseMapper.getWareIdsBySkuId(lock.getSkuId(), lock.getCount());
            stock.setWareIds(wareIds);
            return stock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock collect : collects) {
            List<Long> wareIds = collect.getWareIds();
            Long skuId = collect.getSkuId();
            Integer count = collect.getCount();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            } else {
                boolean hasStock = false;
                for (Long wareId : wareIds) {
                    Integer result = baseMapper.updateStock(skuId, wareId, count);
                    if (result > 0) {
                        hasStock = true;

                        // 告诉rabbitmq，锁定的库存
                        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", count, orderTaskEntity.getId(), wareId, 1);
                        wareOrderTaskDetailService.save(taskDetailEntity);

                        StockLockedTo lockedTo = new StockLockedTo();
                        lockedTo.setId(orderTaskEntity.getId());
                        StockDetailTo detailTo = new StockDetailTo();
                        BeanUtils.copyProperties(taskDetailEntity,detailTo);
                        lockedTo.setDetailTo(detailTo);
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);
                        break;
                    }
                }

                // 只要有一个skuId的商品没有库存，就抛出异常
                if (!hasStock) {
                    throw new NoStockException(skuId);
                }
            }
        }
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        // 解锁
        /**
         * 启动手动ack机制
         *
         * 查询数据库关于这个订单的库存锁定信息
         * 有：库存锁定成功
         *      解锁：查看订单情况
         *          有：订单状态
         *              已取消：解锁
         *              没取消：不解锁
         *          没有：必须解锁
         * 没有：库存锁定失败，无需解锁
         */
        System.out.println("收到库存锁定信息");
        StockDetailTo detailTo = to.getDetailTo();
        Long detailId = detailTo.getId();
        WareOrderTaskDetailEntity entity = wareOrderTaskDetailService.getById(detailId);
        if (entity != null) {
            Long taskId = to.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(taskId);
            // 查询订单状态
            String orderSn = taskEntity.getOrderSn();
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo data = r.getData("data", new TypeReference<OrderVo>() {
                });
                // 订单不存在或者订单被取消，解锁库存
                if (data == null || data.getStatus() == 4) {
                    // 未被解锁才能解锁库存
                    if (detailTo.getLockStatus() == 1) {
                        unLockStock(detailTo.getSkuId(),detailTo.getWareId(),detailTo.getSkuNum(),detailId);
                    }
                } else {
                    throw new RuntimeException("订单存在且未被取消，消息重新入队消费");
                }
            } else {
                throw new RuntimeException("远程查询订单失败，消息重新入队消费");
            }
        }
    }

    /**
     * 防止订单卡顿，库存优先消费，导致库存无法解锁
     * @param to
     */
    @Override
    public void unlockStock(OrderTo to) {
        String orderSn = to.getOrderSn();
        WareOrderTaskEntity task = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));

        Long taskId = task.getId();
        List<WareOrderTaskDetailEntity> taskDetailEntities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", taskId)
                .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity item : taskDetailEntities) {
            // Long skuId,Long wareId,Integer num,Long taskDetailId
            unLockStock(item.getSkuId(), item.getWareId(), item.getSkuNum(), taskId);
        }
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer count;
        private List<Long> wareIds;
    }
}