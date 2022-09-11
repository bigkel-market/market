package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.constant.ProductConstant;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.product.dao.AttrAttrgroupRelationDao;
import com.itchenyang.market.product.dao.AttrDao;
import com.itchenyang.market.product.dao.AttrGroupDao;
import com.itchenyang.market.product.dao.CategoryDao;
import com.itchenyang.market.product.entity.AttrAttrgroupRelationEntity;
import com.itchenyang.market.product.entity.AttrEntity;
import com.itchenyang.market.product.entity.AttrGroupEntity;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.service.AttrService;
import com.itchenyang.market.product.service.CategoryService;
import com.itchenyang.market.product.vo.AttrRespVo;
import com.itchenyang.market.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao relationDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.insert(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(attrType) ? 1 : 0);
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            // 此处使用and，那么and中的条件就被括号包括
            wrapper.and(t -> {
                t.like("attr_name", key);
            });
        }
        // 数据库中查出对应表的数据
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        // 拿到需要返回给前端的数据
        List<AttrRespVo> attrRespVoList = page.getRecords().stream().map(t -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(t, attrRespVo);
            // 查询分组名称
            if (t.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
                AttrAttrgroupRelationEntity attrgroupRelationEntity = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", t.getAttrId()));
                if (attrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(t.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVoList);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        // 查询到分类完整路径
        Long catelogId = attrEntity.getCatelogId();
        List<Long> catelogPath = categoryService.findCatelogPath(catelogId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        attrRespVo.setCatelogName(categoryEntity.getName());

        // 查询分组信息
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        // 更新分组
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());

            Integer count = relationDao.selectCount(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                relationDao.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> selectAttrs(Long attrGroupId) {
        // 根据分组名查询到关系
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));

        // 根据查询到的关系查询具体规则
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (attrIds.size() == 0) {
            return null;
        }

        return attrDao.selectBatchIds(attrIds);
    }

    @Override
    public void removeRelations(List<AttrAttrgroupRelationEntity> entities) {
        relationDao.removeRelations(entities);
    }

    @Override
    public void addRelations(List<AttrAttrgroupRelationEntity> entities) {
        relationDao.addRelations(entities);
    }

    @Override
    public PageUtils selectNotAttrs(Map<String, Object> params, Long attrGroupId) {
        // 1、查询当前分组所属分类catelogId
        AttrGroupEntity curGroup = attrGroupDao.selectById(attrGroupId);
        Long catelogId = curGroup.getCatelogId();

        // 2、查询catelogId下已经被关联的规则
        // 2.1、查询当前catelogId下的所有分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds = groupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2、查询这些分组已经关联的规则
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // 3、排除掉已经被关联的规则和销售规则
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(t -> {
                t.eq("attr_id", key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }
}