package com.itchenyang.market.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

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