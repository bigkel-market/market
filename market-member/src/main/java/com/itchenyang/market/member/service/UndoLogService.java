package com.itchenyang.market.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.member.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:46:11
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

