package com.itchenyang.market.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope    // 配置中心配置修改后，实时刷新
public class MarketCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketCouponApplication.class, args);
    }

}
