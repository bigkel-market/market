package com.itchenyang.market.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.itchenyang.market.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockTo> hasStock(List<Long> skuIds);

    Boolean lockOrderStock(WareSkuLockVo lockVo);
}

