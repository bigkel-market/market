package com.itchenyang.market.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BigKel
 * @createTime 2022/11/5
 */
@Configuration
public class MarketWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        HashMap<String, String> viewMapping = new HashMap<String, String>() {
            {
//                put("login.html", "login");
                put("register.html", "register");
            }
        };

        for (Map.Entry<String, String> item : viewMapping.entrySet()) {
            registry.addViewController(item.getKey()).setViewName(item.getKey());
        }
    }
}
