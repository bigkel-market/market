package com.itchenyang.market.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:43:34
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

