package com.bigkel.market.seckill.controller;

import com.bigkel.market.seckill.service.SeckillService;
import com.bigkel.market.seckill.to.SeckillSkuRedisTo;
import com.itchenyang.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2023/2/23
 */
@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/getCurrentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> result = seckillService.getCurrentSeckillSkus();
        return R.ok().put("data", result);
    }
}
