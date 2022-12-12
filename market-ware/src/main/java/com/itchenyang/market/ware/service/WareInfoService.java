package com.itchenyang.market.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.ware.entity.WareInfoEntity;
import com.itchenyang.market.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取运费和收货地址信息
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

