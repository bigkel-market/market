package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.SkuInfoEntity;
import com.itchenyang.market.product.vo.SkuItemVo;

import java.util.Map;

/**
 * sku信息
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SkuItemVo itemInfo(Long skuId);
}

