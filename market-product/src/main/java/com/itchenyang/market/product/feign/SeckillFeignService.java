package com.itchenyang.market.product.feign;

import com.itchenyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author BigKel
 * @createTime 2023/2/28
 */
@FeignClient("market-seckill")
public interface SeckillFeignService {

    @GetMapping("/getSkuSeckillNotice/{skuId}")
    R getSkuSeckillNotice(@PathVariable("skuId") Long skuId);
}
