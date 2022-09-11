package com.itchenyang.market.product.controller;

import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.product.entity.AttrAttrgroupRelationEntity;
import com.itchenyang.market.product.entity.AttrEntity;
import com.itchenyang.market.product.entity.AttrGroupEntity;
import com.itchenyang.market.product.service.AttrGroupService;
import com.itchenyang.market.product.service.AttrService;
import com.itchenyang.market.product.service.CategoryService;
import com.itchenyang.market.product.vo.AttrGroupAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }

    /**
     * 根据catelogId查询分组以及属性规则
     * /api/product/attrgroup/{catelogId}/withattr
     */
    @RequestMapping("/{catelogId}/withattr")
    public R AttrDetail(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupAttrsVo> data = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", data);
    }

    /**
     * 分组关联规则列表
     */
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R listRelation(@PathVariable("attrGroupId") Long attrGroupId) {
        // 查到当前分组拥有的规则
        List<AttrEntity> attrs = attrService.selectAttrs(attrGroupId);
        return R.ok().put("data", attrs);
    }

    /**
     * 分组可关联规则列表
     */
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R listNotRelation(@RequestParam Map<String, Object> params ,
                             @PathVariable("attrGroupId") Long attrGroupId) {
        // 查到当前分组能够去关联的规则
        PageUtils page = attrService.selectNotAttrs(params, attrGroupId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		// 得到attrGroupId的完整路径
        Long catelogId = attrGroup.getCatelogId();
        attrGroup.setCatelogPath(categoryService.findCatelogPath(catelogId));
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 分组关联规则删除
     */
    @RequestMapping("/attr/relation/delete")
    public R deleteRelations(@RequestBody List<AttrAttrgroupRelationEntity> entities) {
        attrService.removeRelations(entities);

        return R.ok();
    }

    /**
     * 分组关联规则新增
     */
    @RequestMapping("/attr/relation")
    public R addRelations(@RequestBody List<AttrAttrgroupRelationEntity> entities) {
        attrService.addRelations(entities);

        return R.ok();
    }

}
