package com.itchenyang.controller;

import com.itchenyang.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BigKel
 * @createTime 2022/11/5
 */
@RestController
@RequestMapping("/sms")
public class smsController {

    /**
     * 提供给别的服务调用
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        System.out.println("验证码为: " + code);
        return R.ok();
    }
}
