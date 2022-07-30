package com.itchenyang.market.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:47:42
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

