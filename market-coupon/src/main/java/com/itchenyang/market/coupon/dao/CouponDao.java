package com.itchenyang.market.coupon.dao;

import com.itchenyang.market.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:43:34
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
