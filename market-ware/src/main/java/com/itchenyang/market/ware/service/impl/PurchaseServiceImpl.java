package com.itchenyang.market.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.constant.WareConstant;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.ware.dao.PurchaseDao;
import com.itchenyang.market.ware.entity.PurchaseDetailEntity;
import com.itchenyang.market.ware.entity.PurchaseEntity;
import com.itchenyang.market.ware.entity.WareSkuEntity;
import com.itchenyang.market.ware.feign.ProductFeignService;
import com.itchenyang.market.ware.service.PurchaseDetailService;
import com.itchenyang.market.ware.service.PurchaseService;
import com.itchenyang.market.ware.service.WareSkuService;
import com.itchenyang.market.ware.vo.MergeVo;
import com.itchenyang.market.ware.vo.PurchaseDetailDoneVo;
import com.itchenyang.market.ware.vo.PurchaseDoneVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDetailService purchaseDetailService;

    @Resource
    private WareSkuService wareSkuService;

    @Resource
    private PurchaseService purchaseService;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void Merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            // 新增采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }

        // 合并到采购单——>更改采购需求的采购单字段为当前采购单字段
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> list = mergeVo.getItems().stream().map(one -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(one);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(list);

        // 更新采购单时间
        PurchaseEntity update = new PurchaseEntity();
        update.setId(purchaseId);
        update.setUpdateTime(new Date());
        this.updateById(update);
    }

    @Transactional
    @Override
    public void receiveOrders(List<Long> ids) {
        // 1、更改采购需求的状态为正在采购
        List<PurchaseDetailEntity> purchaseDetailEntities = ids.stream().map(id -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setPurchaseId(id);
            entity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return entity;
        }).collect(Collectors.toList());
        baseMapper.updateStatusByPurchaseId(purchaseDetailEntities);

        // 2、更改采购单的状态为已领取
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(id);
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return purchaseEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(purchaseEntities);
    }

    @Transactional
    @Override
    public void doneOrder(PurchaseDoneVo doneVo) {
        // 1、判断采购项是否有问题  采购项->采购失败  采购单->有异常，表同步状态
        // 2、采购项采购成功  入库 wms_ware_sku
        AtomicReference<Boolean> flag = new AtomicReference<>(Boolean.TRUE);
        List<PurchaseDetailDoneVo> detailDoneVos = doneVo.getItems();
        detailDoneVos.forEach(detailVo -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(detailVo.getItemId());
            purchaseDetailEntity.setStatus(detailVo.getStatus());
            if (detailVo.getStatus() == WareConstant.PurchaseDetailStatusEnum.FINISH.getCode()) {
                // 入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(detailVo.getItemId());
                WareSkuEntity hasWare = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>()
                        .eq("sku_id", detailEntity.getSkuId())
                        .eq("ware_id", detailEntity.getWareId()));
                WareSkuEntity wareSkuEntity = new WareSkuEntity();
                wareSkuEntity.setSkuId(detailEntity.getSkuId());
                wareSkuEntity.setWareId(detailEntity.getWareId());
                try {
                    R skuInfo = productFeignService.skuInfo(detailEntity.getSkuId());
                    Map<String, Object> data = (Map<String, Object>) skuInfo.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.getOrDefault("skuName", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (hasWare != null) {
                    wareSkuEntity.setId(hasWare.getId());
                    wareSkuEntity.setStock(hasWare.getStock() + detailEntity.getSkuNum());
                    wareSkuService.updateById(wareSkuEntity);
                } else {
                    wareSkuEntity.setStock(detailEntity.getSkuNum());
                    wareSkuService.save(wareSkuEntity);
                }
            } else {
                flag.set(Boolean.FALSE);
            }
            purchaseDetailService.updateById(purchaseDetailEntity);
        });
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        if (flag.get()) {
            // 采购单正常
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
            purchaseEntity.setUpdateTime(new Date());
        } else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
            purchaseEntity.setUpdateTime(new Date());
        }
        purchaseService.updateById(purchaseEntity);
    }
}