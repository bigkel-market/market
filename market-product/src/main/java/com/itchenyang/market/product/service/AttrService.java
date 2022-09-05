package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.AttrAttrgroupRelationEntity;
import com.itchenyang.market.product.entity.AttrEntity;
import com.itchenyang.market.product.vo.AttrResp;
import com.itchenyang.market.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBasePage(Map<String, Object> params, Long catelogId, String attrType);

    AttrResp getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> selectAttrs(Long attrGroupId);

    void removeRelations(List<AttrAttrgroupRelationEntity> entities);

    PageUtils selectNotAttrs(Map<String, Object> params, Long attrGroupId);

    void addRelations(List<AttrAttrgroupRelationEntity> entities);
}

