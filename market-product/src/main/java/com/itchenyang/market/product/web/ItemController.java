package com.itchenyang.market.product.web;

import com.itchenyang.market.product.service.SkuInfoService;
import com.itchenyang.market.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author BigKel
 * @createTime 2022/10/22
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String getItem(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVo data = skuInfoService.itemInfo(skuId);
        model.addAttribute("item", data);
        return "item";
    }
}
