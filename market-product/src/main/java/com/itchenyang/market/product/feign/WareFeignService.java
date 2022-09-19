package com.itchenyang.market.product.feign;

import com.itchenyang.common.to.SkuHasStockTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
@FeignClient("market-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    List<SkuHasStockTo> skuHasStock(@RequestBody List<Long> skuIds);
}
