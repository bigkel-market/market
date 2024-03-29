package com.itchenyang.market.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@EnableFeignClients(basePackages = "com.itchenyang.market.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class MarketWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketWareApplication.class, args);
    }

}
