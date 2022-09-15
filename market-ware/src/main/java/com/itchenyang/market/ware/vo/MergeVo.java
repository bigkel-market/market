package com.itchenyang.market.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/12
 */
@Data
public class MergeVo {
    /**
     * 采购单id
     */
    private Long purchaseId;

    /**
     * 采购需求id
     */
    private List<Long> items;
}
