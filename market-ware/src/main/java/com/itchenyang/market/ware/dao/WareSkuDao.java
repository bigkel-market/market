package com.itchenyang.market.ware.dao;

import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
