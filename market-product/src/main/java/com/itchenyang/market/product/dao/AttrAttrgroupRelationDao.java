package com.itchenyang.market.product.dao;

import com.itchenyang.market.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void removeRelations(@Param("entities") List<AttrAttrgroupRelationEntity> entities);

    void addRelations(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
