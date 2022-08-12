package com.itchenyang.market.coupon.controller;

import com.itchenyang.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("coupon/coupon")
@RefreshScope
public class Atest {

    @Value("${first.name}")
    private String value;

    @RequestMapping("/test")
    public R test() {
        return R.ok().put("name", value);
    }
}
