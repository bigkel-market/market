package com.itchenyang.market.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author BigKel
 * @createTime 2022/11/5
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.itchenyang.market.auth.feign")
@EnableRedisHttpSession
public class MarketAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketAuthServerApplication.class, args);
    }
}
