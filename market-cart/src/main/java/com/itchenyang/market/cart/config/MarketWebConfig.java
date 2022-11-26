package com.itchenyang.market.cart.config;

import com.itchenyang.market.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
                put("success.html", "success");
//                put("cart.html", "cartList");
            }
        };

        for (Map.Entry<String, String> item : viewMapping.entrySet()) {
            registry.addViewController(item.getKey()).setViewName(item.getKey());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
