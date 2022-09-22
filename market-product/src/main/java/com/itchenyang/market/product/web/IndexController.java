package com.itchenyang.market.product.web;

import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.entity.Catelog2Vo;
import com.itchenyang.market.product.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author BigKel
 * @createTime 2022/9/20
 */
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }
}
