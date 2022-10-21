package com.itchenyang.market.search.controller;

import com.itchenyang.market.search.service.MallSearchService;
import com.itchenyang.market.search.vo.SearchParam;
import com.itchenyang.market.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author BigKel
 * @createTime 2022/10/12
 */
@Controller
public class SearchController {

    @Resource
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String index(SearchParam param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
