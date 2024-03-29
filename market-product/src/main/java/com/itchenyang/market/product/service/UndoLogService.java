package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

