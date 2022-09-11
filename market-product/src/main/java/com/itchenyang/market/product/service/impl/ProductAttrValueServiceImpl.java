package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.product.dao.ProductAttrValueDao;
import com.itchenyang.market.product.entity.AttrEntity;
import com.itchenyang.market.product.entity.ProductAttrValueEntity;
import com.itchenyang.market.product.service.AttrService;
import com.itchenyang.market.product.service.ProductAttrValueService;
import com.itchenyang.market.product.vo.BaseAttrs;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuAttr(Long id, List<BaseAttrs> baseAttrs) {
        if (baseAttrs != null && baseAttrs.size() != 0) {
            List<ProductAttrValueEntity> list = baseAttrs.stream().map(one -> {
                ProductAttrValueEntity entity = new ProductAttrValueEntity();
                entity.setSpuId(id);
                entity.setAttrId(one.getAttrId());
                AttrEntity attrEntity = attrService.getById(one.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrValue(one.getAttrValues());
                entity.setQuickShow(one.getShowDesc());
                return entity;
            }).collect(Collectors.toList());
            this.saveBatch(list);
        }
    }

}