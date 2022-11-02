package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.product.dao.AttrGroupDao;
import com.itchenyang.market.product.entity.AttrEntity;
import com.itchenyang.market.product.entity.AttrGroupEntity;
import com.itchenyang.market.product.service.AttrGroupService;
import com.itchenyang.market.product.service.AttrService;
import com.itchenyang.market.product.vo.AttrGroupAttrsVo;
import com.itchenyang.market.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        } else {
            String key = (String) params.get("key");
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
            if (!StringUtils.isBlank(key)) {
                wrapper.and(obj -> {
                    obj.like("attr_group_name", key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> groups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        return groups.stream().map(one -> {
            AttrGroupAttrsVo groupAttrsVo = new AttrGroupAttrsVo();
            BeanUtils.copyProperties(one, groupAttrsVo);
            List<AttrEntity> attrs = attrService.selectAttrs(one.getAttrGroupId());
            groupAttrsVo.setAttrs(attrs);
            return groupAttrsVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}