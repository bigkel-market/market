package com.itchenyang.common.to;

import lombok.Data;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
@Data
public class SkuHasStockTo {

    private Long SkuId;

    private Boolean stock;
}
