package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.ProductAttrValueEntity;
import com.itchenyang.market.product.vo.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuAttr(Long id, List<BaseAttrs> baseAttrs);
}

