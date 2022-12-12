package com.itchenyang.market.order.feign;

import com.itchenyang.common.to.SkuHasStockTo;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/12/5
 */
@FeignClient("market-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    List<SkuHasStockTo> skuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping(value = "/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/stock")
    R lockOrderStock(@RequestBody WareSkuLockVo lockVo);
}
