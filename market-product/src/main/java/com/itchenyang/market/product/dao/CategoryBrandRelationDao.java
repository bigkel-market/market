package com.itchenyang.market.product.dao;

import com.itchenyang.market.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCasdaCategory(@Param("catelogId") Long catId, @Param("catelogName") String name);

    void updateCasdaBrand(@Param("brandId") Long brandId, @Param("brandName") String name);
}
