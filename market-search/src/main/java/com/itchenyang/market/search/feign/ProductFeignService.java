package com.itchenyang.market.search.feign;

import com.itchenyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/10/19
 */
@FeignClient("market-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/brandsInfo")
    R brandsInfo(@RequestParam("brandIds") List<Long> brandIds);
}
