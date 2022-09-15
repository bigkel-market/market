package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.to.SkuReductionTo;
import com.itchenyang.common.to.SpuBoundTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.product.dao.SpuInfoDao;
import com.itchenyang.market.product.entity.*;
import com.itchenyang.market.product.feign.CouponFeignService;
import com.itchenyang.market.product.service.*;
import com.itchenyang.market.product.vo.Images;
import com.itchenyang.market.product.vo.Skus;
import com.itchenyang.market.product.vo.SpuSaveVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService infoDescService;

    @Resource
    private SpuImagesService imagesService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);

        // 2、保存spu图片描述 pms_spu_info_desc
        SpuInfoDescEntity imageDes = new SpuInfoDescEntity();
        imageDes.setSpuId(spuInfoEntity.getId());
        imageDes.setDecript(String.join(",", vo.getDecript()));
        infoDescService.save(imageDes);

        // 3、保存spu图片集 pms_spu_images
        imagesService.saveImages(spuInfoEntity.getId(), vo.getImages());

        // 4、保存spu的规格参数 pms_product_attr_value
        productAttrValueService.saveSpuAttr(spuInfoEntity.getId(), vo.getBaseAttrs());

        // 5、保存spu的积分信息 market_server_coupon -> sms_spu_bounds
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(vo.getBounds(), spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R spuRes = couponFeignService.saveSpuBounds(spuBoundTo);
        if (spuRes.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        // 6、保存当前spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        skus.forEach(sku -> {
            // 6.1、sku基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfoEntity);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            Images defaultImage = sku.getImages().stream().filter(img -> img.getDefaultImg() == 1).findFirst().orElseGet(() -> {
                Images images = new Images();
                images.setImgUrl("");
                return images;
            });
            skuInfoEntity.setSkuDefaultImg(defaultImage.getImgUrl());
            skuInfoService.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            // 6.2、sku图片信息 pms_sku_images
            List<SkuImagesEntity> saveImages = sku.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                skuImagesEntity.setImgUrl(img.getImgUrl());
                return skuImagesEntity;
            }).filter(one -> StringUtils.isNotBlank(one.getImgUrl())).collect(Collectors.toList());
            skuImagesService.saveBatch(saveImages);

            // 6.3、sku销售属性信息 pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> saveAttr = sku.getAttr().stream().map(attr -> {
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, entity);
                entity.setSkuId(attr.getAttrId());
                return entity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(saveAttr);

            // 6.4、sku的优惠/满减信息 market_server_coupon -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(sku, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                R skuRes = couponFeignService.saveSkuReduction(skuReductionTo);
                if (skuRes.getCode() != 0) {
                    log.error("远程保存sku优惠信息失败");
                }
            }
        });
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(t -> {
                t.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }
}