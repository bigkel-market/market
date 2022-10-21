package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.product.dao.BrandDao;
import com.itchenyang.market.product.dao.CategoryBrandRelationDao;
import com.itchenyang.market.product.entity.BrandEntity;
import com.itchenyang.market.product.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateCasda(BrandEntity brand) {
        baseMapper.updateById(brand);
        // 级联更新
        if (!StringUtils.isBlank(brand.getName())) {
            categoryBrandRelationDao.updateCasdaBrand(brand.getBrandId(), brand.getName());
        }
    }

    @Override
    public List<BrandEntity> brandsInfo(List<Long> brandIds) {
        List<BrandEntity> brands = baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
        return brands;
    }

}