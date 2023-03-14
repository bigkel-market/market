package com.itchenyang.market.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.product.dao.SkuInfoDao;
import com.itchenyang.market.product.entity.SkuImagesEntity;
import com.itchenyang.market.product.entity.SkuInfoEntity;
import com.itchenyang.market.product.entity.SpuInfoDescEntity;
import com.itchenyang.market.product.feign.SeckillFeignService;
import com.itchenyang.market.product.service.*;
import com.itchenyang.market.product.vo.SeckillSkuVo;
import com.itchenyang.market.product.vo.SkuItemSaleAttrVo;
import com.itchenyang.market.product.vo.SkuItemVo;
import com.itchenyang.market.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SkuItemVo itemInfo(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        //1、sku基本信息的获取  pms_sku_info
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = baseMapper.selectById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        /**
         * 可以单独完成
         */
        //2、sku的图片信息    pms_sku_images
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImages = skuImagesService.getImages(skuId);
            skuItemVo.setImages(skuImages);
        }, executor);

        /**
         *  下面三个任务都得等第一个任务完成
         */
        //3、获取spu的销售属性组合
        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SkuItemSaleAttrVo> saleAttrs = skuSaleAttrValueService.getSaleAttrGroupBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrs);
        }, executor);

        //4、获取spu的介绍
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            SpuInfoDescEntity spuDesc = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuDesc);
        }, executor);

        //5、获取spu的规格参数信息
        CompletableFuture<Void> groupFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SpuItemAttrGroupVo> value = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(value);
        }, executor);

        // 6、查询当前商品的秒杀信息
        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            R r = seckillFeignService.getSkuSeckillNotice(skuId);
            if (r.getCode() == 0) {
                SeckillSkuVo seckillSkuVo = r.getData("data", new TypeReference<SeckillSkuVo>() {
                });
                skuItemVo.setSeckillSkuVo(seckillSkuVo);
            }
        }, executor);

        try {
            CompletableFuture.allOf(saleFuture, descFuture, groupFuture, imageFuture, seckillFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return skuItemVo;
    }

}