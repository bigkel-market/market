package com.itchenyang.market.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:43:34
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

