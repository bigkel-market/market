package com.itchenyang.market.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

