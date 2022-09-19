package com.itchenyang.market.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.ware.dao.WareSkuDao;
import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.itchenyang.market.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

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
}