package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.entity.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface CategoryService extends IService<CategoryEntity> {

    List<CategoryEntity> selectByTree();

    void removeMenuByIds(List<Long> ids);

    List<Long> findCatelogPath(Long catelogId);

    void updateCasda(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2Vo>> getCatelogJson();
}

