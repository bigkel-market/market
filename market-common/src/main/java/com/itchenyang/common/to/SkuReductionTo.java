package com.itchenyang.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    private Long skuId;

    private int fullCount;
    private BigDecimal discount;
    // 优惠(满多少折扣多少)————是否与其他优惠叠加
    private int countStatus;


    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    // 优惠(满多少减多少)————是否与其他优惠叠加
    private int priceStatus;

    private List<MemberPrice> memberPrice;
}
