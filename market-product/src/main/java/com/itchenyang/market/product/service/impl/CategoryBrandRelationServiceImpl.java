package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.product.dao.BrandDao;
import com.itchenyang.market.product.dao.CategoryBrandRelationDao;
import com.itchenyang.market.product.dao.CategoryDao;
import com.itchenyang.market.product.entity.BrandEntity;
import com.itchenyang.market.product.entity.CategoryBrandRelationEntity;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.service.BrandService;
import com.itchenyang.market.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    private BrandDao brandDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Resource
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> getBrands(Long cateId) {
        List<CategoryBrandRelationEntity> relationEntityList = categoryBrandRelationDao.selectList(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", cateId));
        List<Long> brandIds = relationEntityList.stream()
                .map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        List<BrandEntity> brands = new ArrayList<>();
        if (brandIds.size() != 0) {
             brands = brandService.listByIds(brandIds);
        }
        return brands;
    }

}