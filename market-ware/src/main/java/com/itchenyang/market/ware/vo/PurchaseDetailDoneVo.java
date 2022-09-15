package com.itchenyang.market.ware.vo;

import lombok.Data;

/**
 * @author BigKel
 * @createTime 2022/9/14
 */
@Data
public class PurchaseDetailDoneVo {

    private Long itemId;

    private Integer status;

    private String reason;
}
