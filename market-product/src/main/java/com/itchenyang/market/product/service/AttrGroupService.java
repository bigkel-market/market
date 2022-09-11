package com.itchenyang.market.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.market.product.entity.AttrGroupEntity;
import com.itchenyang.market.product.vo.AttrGroupAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

