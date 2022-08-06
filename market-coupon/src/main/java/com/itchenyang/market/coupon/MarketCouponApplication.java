package com.itchenyang.market.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketCouponApplication.class, args);
    }

}
