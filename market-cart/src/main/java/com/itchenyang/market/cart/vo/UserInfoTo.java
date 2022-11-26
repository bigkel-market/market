package com.itchenyang.market.cart.vo;

import lombok.Data;

/**
 * @author BigKel
 * @createTime 2022/11/18
 */
@Data
public class UserInfoTo {

    private Long userId;

    private String userKey;

    private Boolean tempUser = false;     // 是否有临时用户
}
