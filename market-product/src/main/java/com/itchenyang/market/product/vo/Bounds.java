package com.itchenyang.market.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {

    private BigDecimal buyBounds;
    private BigDecimal growBounds;

}