package com.bigkel.market.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author BigKel
 * @createTime 2022/10/9
 */
@Configuration
public class MyRedissonConfig {

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost + ":6379");
        config.useSingleServer().setPassword(redisPassword);
        return Redisson.create(config);
    }
}
