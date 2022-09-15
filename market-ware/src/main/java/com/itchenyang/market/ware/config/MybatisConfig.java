package com.itchenyang.market.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.itchenyang.market.ware.dao")
public class MybatisConfig {

    @Bean
    public PaginationInterceptor paginationInnerInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页之后，true调回到首页，false继续请求。默认false
        interceptor.setOverflow(true);
        // 设置最大单页显示数量，默认500， -1不受限制
        interceptor.setLimit(1000);
        return interceptor;

    }
}
