package com.itchenyang.market.product.app;

import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.R;
import com.itchenyang.common.valid.AddGroupVaild;
import com.itchenyang.common.valid.UpdateGroupVaild;
import com.itchenyang.common.valid.UpdateStatusGroupVaild;
import com.itchenyang.market.product.entity.BrandEntity;
import com.itchenyang.market.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    @RequestMapping("/brandsInfo")
    public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds) {
        List<BrandEntity> brands = brandService.brandsInfo(brandIds);
        return R.ok().put("brands", brands);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {AddGroupVaild.class})/**@Valid**/ @RequestBody BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {UpdateGroupVaild.class, UpdateStatusGroupVaild.class}) @RequestBody BrandEntity brand){
        brandService.updateCasda(brand);
        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(value = {UpdateStatusGroupVaild.class}) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
