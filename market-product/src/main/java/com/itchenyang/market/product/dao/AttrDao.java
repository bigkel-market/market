package com.itchenyang.market.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itchenyang.market.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> listCanElastic(@Param("attrIds") List<Long> attrIds);
}
