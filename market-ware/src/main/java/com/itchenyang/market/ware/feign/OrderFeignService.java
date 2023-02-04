package com.itchenyang.market.ware.feign;

import com.itchenyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author BigKel
 * @createTime 2023/2/1
 */
@FeignClient("market-order")
public interface OrderFeignService {

    @GetMapping(value = "/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
