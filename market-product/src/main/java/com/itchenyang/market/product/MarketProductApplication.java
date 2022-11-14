package com.itchenyang.market.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableCaching
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.itchenyang.market.product.feign")
@EnableRedisHttpSession
public class MarketProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketProductApplication.class, args);
    }

}
