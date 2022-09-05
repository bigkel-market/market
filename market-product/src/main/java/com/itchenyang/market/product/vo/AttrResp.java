package com.itchenyang.market.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrResp extends AttrVo{
    /**
     * 分类名称
     * 手机/数码/手机
     */
    private String catelogName;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分类完整路径
     */
    private List<Long> catelogPath;
}
