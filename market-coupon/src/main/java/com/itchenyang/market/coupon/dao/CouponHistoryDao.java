package com.itchenyang.market.coupon.dao;

import com.itchenyang.market.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:43:34
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
