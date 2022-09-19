package com.itchenyang.market.product.feign;

import com.itchenyang.common.to.es.SkuEsModel;
import com.itchenyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
@FeignClient("market-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R saveProduct(@RequestBody List<SkuEsModel> models);
}
