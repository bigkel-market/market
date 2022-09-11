package com.itchenyang.market.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.to.MemberPrice;
import com.itchenyang.common.to.SkuReductionTo;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.coupon.dao.SkuFullReductionDao;
import com.itchenyang.market.coupon.entity.MemberPriceEntity;
import com.itchenyang.market.coupon.entity.SkuFullReductionEntity;
import com.itchenyang.market.coupon.entity.SkuLadderEntity;
import com.itchenyang.market.coupon.service.MemberPriceService;
import com.itchenyang.market.coupon.service.SkuFullReductionService;
import com.itchenyang.market.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    private SkuLadderService skuLadderService;

    @Resource
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // sku的优惠/满减信息 market_server_coupon -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
        // sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> list = memberPrice.stream().map(one -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(one.getId());
            memberPriceEntity.setMemberLevelName(one.getName());
            memberPriceEntity.setMemberPrice(one.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(one -> one.getMemberPrice().compareTo(new BigDecimal(0)) > 0).collect(Collectors.toList());
        memberPriceService.saveBatch(list);
    }

}