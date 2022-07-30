package com.itchenyang.market.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itchenyang.market.product.entity.FlywaySchemaHistoryEntity;
import com.itchenyang.market.product.service.FlywaySchemaHistoryService;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.R;



/**
 * 
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@RestController
@RequestMapping("product/flywayschemahistory")
public class FlywaySchemaHistoryController {
    @Autowired
    private FlywaySchemaHistoryService flywaySchemaHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:flywayschemahistory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = flywaySchemaHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{installedRank}")
    // @RequiresPermissions("product:flywayschemahistory:info")
    public R info(@PathVariable("installedRank") Integer installedRank){
		FlywaySchemaHistoryEntity flywaySchemaHistory = flywaySchemaHistoryService.getById(installedRank);

        return R.ok().put("flywaySchemaHistory", flywaySchemaHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:flywayschemahistory:save")
    public R save(@RequestBody FlywaySchemaHistoryEntity flywaySchemaHistory){
		flywaySchemaHistoryService.save(flywaySchemaHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:flywayschemahistory:update")
    public R update(@RequestBody FlywaySchemaHistoryEntity flywaySchemaHistory){
		flywaySchemaHistoryService.updateById(flywaySchemaHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:flywayschemahistory:delete")
    public R delete(@RequestBody Integer[] installedRanks){
		flywaySchemaHistoryService.removeByIds(Arrays.asList(installedRanks));

        return R.ok();
    }

}
