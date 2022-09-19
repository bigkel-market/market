package com.itchenyang.market.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itchenyang.market.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("spuUp") Integer spuUp);
}
