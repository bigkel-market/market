package com.itchenyang.market.order.feign;

import com.itchenyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author BigKel
 * @createTime 2022/12/8
 */
@FeignClient("market-product")
public interface ProductFeignService {

    @RequestMapping("/product/spuinfo/skuId/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
