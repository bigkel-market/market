package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.constant.ProductConstant;
import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.to.SkuReductionTo;
import com.itchenyang.common.to.SpuBoundTo;
import com.itchenyang.common.to.es.SkuEsModel;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.product.dao.AttrDao;
import com.itchenyang.market.product.dao.SpuInfoDao;
import com.itchenyang.market.product.entity.*;
import com.itchenyang.market.product.feign.CouponFeignService;
import com.itchenyang.market.product.feign.SearchFeignService;
import com.itchenyang.market.product.feign.WareFeignService;
import com.itchenyang.market.product.service.*;
import com.itchenyang.market.product.vo.Images;
import com.itchenyang.market.product.vo.Skus;
import com.itchenyang.market.product.vo.SpuSaveVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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

    @Resource
    private AttrDao attrDao;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private SearchFeignService searchFeignService;

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
                entity.setSkuId(skuId);
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

    @Override
    public void up(Long spuId) {
        // 1、查出当前spuId下所有的sku信息
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(
                new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        // 拿到spuId下可供检索的attr信息
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        List<Long> attrIds = productAttrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> canElasticAttrIds = attrDao.listCanElastic(attrIds);
        Set<Long> canElasticAttrIdSets = new HashSet<>(canElasticAttrIds);
        List<SkuEsModel.Attrs> skuEsAttrs = productAttrValueEntities.stream()
                .filter(item -> canElasticAttrIdSets.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(Collectors.toList());

        // 远程调用查询每个sku是否有库存
        Map<Long, Boolean> hasStockMap = new HashMap<>();
        try {
            List<SkuHasStockTo> skuHasStockList = wareFeignService.skuHasStock(skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
            hasStockMap = skuHasStockList.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getStock));
        } catch (Exception e) {
            log.error("远程查询商品是否有库存异常, {}", e);
        }

        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        // 2、封装每个sku信息进 SkuEsModel
        List<SkuEsModel> esModels = skuInfoEntities.stream().map(item -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(item, esModel);

            // skuPrice, skuImg
            esModel.setSkuPrice(item.getPrice());
            esModel.setSkuImg(item.getSkuDefaultImg());

            // hasStock, hotScore
            esModel.setHotScore(0L);
            // 远程调用，库存系统查看是否有库存
//            esModel.setHasStock(finalHasStockMap.getOrDefault(item.getSkuId(), true));
            esModel.setHasStock(true);
            // 查询品牌信息和分类信息
            BrandEntity brands = brandService.getById(item.getBrandId());
            esModel.setBrandName(brands.getName());
            esModel.setBrandImg(brands.getLogo());

            CategoryEntity categorys = categoryService.getById(item.getCatalogId());
            esModel.setCatalogName(categorys.getName());

            // attr相关信息
            esModel.setAttrs(skuEsAttrs);
            return esModel;
        }).collect(Collectors.toList());

        // 3、将数据发送给market-search，让es进行保存
        try {
            R r = searchFeignService.saveProduct(esModels);
            if (r.getCode() == 0) {
                // 更改spu信息为上架状态
                baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
            }
        } catch (UncategorizedSQLException e) {
            log.error("更改上架状态异常: ", e);
        } catch (Exception e) {
            log.error("远程调用ElasticSearch保存product接口异常: ", e);
        }
    }
}