package com.bigkel.market.seckill.controller;

import com.bigkel.market.seckill.service.SeckillService;
import com.bigkel.market.seckill.to.SeckillSkuRedisTo;
import com.itchenyang.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2023/2/23
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @ResponseBody
    @GetMapping("/getCurrentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> result = seckillService.getCurrentSeckillSkus();
        return R.ok().put("data", result);
    }

    /**
     * 秒杀活动预告
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/getSkuSeckillNotice/{skuId}")
    public R getSkuSeckillNotice(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo  result = seckillService.getSkuSeckillNotice(skuId);
        return R.ok().put("data", result);
    }

    /**
     * 商品进行秒杀(秒杀开始)
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping(value = "/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {

        String orderSn = null;
        try {
            //1、判断是否登录
            orderSn = seckillService.kill(killId,key,num);
            model.addAttribute("orderSn",orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}
