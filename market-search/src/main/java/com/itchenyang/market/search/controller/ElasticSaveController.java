package com.itchenyang.market.search.controller;

import com.itchenyang.common.exception.BizCodeEnum;
import com.itchenyang.common.to.es.SkuEsModel;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.search.service.ElasticSaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Resource
    private ElasticSaveService elasticSaveService;

    @PostMapping("/product")
    public R saveProduct(@RequestBody List<SkuEsModel> models) {
        boolean b = false;
        try {
            b = elasticSaveService.saveProduct(models);
        } catch (Exception e) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (b) {
            return R.ok();
        }
        return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
