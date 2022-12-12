package com.itchenyang.market.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.exception.NoStockException;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.ware.dao.WareSkuDao;
import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.itchenyang.market.ware.service.WareSkuService;
import com.itchenyang.market.ware.vo.OrderItemVo;
import com.itchenyang.market.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

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

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private Integer count;
        private List<Long> wareIds;
    }
}