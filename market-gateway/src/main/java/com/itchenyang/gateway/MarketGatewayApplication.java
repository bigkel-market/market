package com.itchenyang.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MarketGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketGatewayApplication.class, args);
    }

}
