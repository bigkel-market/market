package com.itchenyang.market.product;

import com.itchenyang.market.product.entity.BrandEntity;
import com.itchenyang.market.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MarketProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("bigkel");
        brandService.save(brandEntity);
    }

}
