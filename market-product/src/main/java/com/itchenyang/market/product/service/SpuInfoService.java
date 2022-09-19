package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.SpuInfoEntity;
import com.itchenyang.market.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);
}

