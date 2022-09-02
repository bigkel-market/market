package com.itchenyang.market.product.service.impl;

import com.itchenyang.market.product.dao.CategoryBrandRelationDao;
import com.itchenyang.market.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;

import com.itchenyang.market.product.dao.CategoryDao;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


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