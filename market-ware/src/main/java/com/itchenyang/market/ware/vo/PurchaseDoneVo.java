package com.itchenyang.market.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/14
 */
@Data
public class PurchaseDoneVo {
    private Long id;

    private List<PurchaseDetailDoneVo> items;
}
