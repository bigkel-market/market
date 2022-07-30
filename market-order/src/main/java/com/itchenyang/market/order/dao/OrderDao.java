package com.itchenyang.market.order.dao;

import com.itchenyang.market.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:47:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
