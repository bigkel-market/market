package com.itchenyang.market.cart.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author BigKel
 * @createTime 2022/11/12
 */
@Configuration
public class MarketSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("MARKETSESSION");
        cookieSerializer.setDomainName("bigkel.com");
        return cookieSerializer;
    }

    // session  setAttrbution时，序列化方式
    @Bean
    public RedisSerializer<?> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}
