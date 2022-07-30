package com.itchenyang.market.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

