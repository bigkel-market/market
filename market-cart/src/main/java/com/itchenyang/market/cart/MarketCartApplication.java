package com.itchenyang.market.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.itchenyang.market.cart.feign")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MarketCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketCartApplication.class, args);
    }

}
