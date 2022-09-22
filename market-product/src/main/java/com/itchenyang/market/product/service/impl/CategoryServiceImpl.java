package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.market.product.dao.CategoryBrandRelationDao;
import com.itchenyang.market.product.dao.CategoryDao;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.entity.Catelog2Vo;
import com.itchenyang.market.product.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public List<CategoryEntity> selectByTree() {
        List<CategoryEntity> all = baseMapper.selectList(null);
        // 找出第一层级
        List<CategoryEntity> treeList = all.stream()
                .filter(one -> one.getParentCid() == 0)
                .map(one -> {
                    one.setChildren(getChildren(one, all));
                    return one;
                })    // 设置children
                .sorted((o1, o2) -> {
                    return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
                })
                .collect(Collectors.toList());
        return treeList;
    }

    @Override
    public void removeMenuByIds(List<Long> ids) {
        // todo 判断是否允许被删除

        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        List<Long> catePath = new ArrayList<>();
        findParentPath(catelogId, catePath);

        // 倒序
        Collections.reverse(catePath);
        return catePath;
    }

    @Transactional
    @Override
    public void updateCasda(CategoryEntity category) {
        baseMapper.updateById(category);
        // 级联更新
        if (!StringUtils.isBlank(category.getName())) {
            categoryBrandRelationDao.updateCasdaCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        // 查出所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();

        // 封装返回的数据
        Map<String, List<Catelog2Vo>> map = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询二级节点
            List<CategoryEntity> level2Categorys = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> level2 = new ArrayList<>();
            if (level2Categorys != null) {
                 level2 = level2Categorys.stream().map(l2 -> {
                    // 父级catId, 子级列表, 本级catId, 本级name
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Categorys = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3Categorys != null) {
                        List<Catelog2Vo.Catelog3Vo> level3 = level3Categorys.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(level3);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return level2;
        }));
        return map;
    }

    public void findParentPath(Long id, List<Long> catePath) {
        catePath.add(id);
        CategoryEntity current = baseMapper.selectById(id);
        if (current.getParentCid() != 0) {
            findParentPath(current.getParentCid(), catePath);
        }
    }

    public List<CategoryEntity> getChildren(CategoryEntity cur, List<CategoryEntity> all) {
        List<CategoryEntity> treeList = all.stream()
                .filter(one -> one.getParentCid().equals(cur.getCatId()))
                .map(one -> {
                    one.setChildren(getChildren(one, all));
                    return one;
                })    // 设置children
                .sorted((o1, o2) -> {
                    return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
                })
                .collect(Collectors.toList());
        return treeList;
    }
}