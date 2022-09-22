package com.itchenyang.market.product.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.product.entity.BrandEntity;
import com.itchenyang.market.product.entity.CategoryBrandRelationEntity;
import com.itchenyang.market.product.service.CategoryBrandRelationService;
import com.itchenyang.market.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 当前分类下的品牌信息
     */
    @RequestMapping("/brands/list")
    public R brandsByCatelog(@RequestParam(value = "catId") Long cateId){
        List<BrandEntity> brands = categoryBrandRelationService.getBrands(cateId);
        List<BrandVo> brandInfo = brands.stream().map(one -> {
            BrandVo vo = new BrandVo();
            vo.setBrandId(one.getBrandId());
            vo.setBrandName(one.getName());
            return vo;
        }).collect(Collectors.toList());
        return R.ok().put("data", brandInfo);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据品牌id查询其已有的关联的分类
     * @param brandId
     * @return
     */
    @GetMapping("/catelog/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R relationlist(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> casda = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));

        return R.ok().put("data", casda);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
