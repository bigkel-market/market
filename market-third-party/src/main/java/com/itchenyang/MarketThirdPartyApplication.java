package com.itchenyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketThirdPartyApplication.class, args);
    }

}
