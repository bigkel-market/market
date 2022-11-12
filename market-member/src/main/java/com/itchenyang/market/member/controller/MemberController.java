package com.itchenyang.market.member.controller;

import com.itchenyang.common.exception.BizCodeEnum;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.member.entity.MemberEntity;
import com.itchenyang.market.member.exception.PhoneExistException;
import com.itchenyang.market.member.exception.UserNameExistException;
import com.itchenyang.market.member.service.MemberService;
import com.itchenyang.market.member.vo.UserLoginVo;
import com.itchenyang.market.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:46:11
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo vo) {
        Boolean status = memberService.login(vo);
        return status ? R.ok() : R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
    }

    /**
     * 注册
     */
    @PostMapping("/regist")
    public R regist(@RequestBody UserRegisterVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXISTS_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXISTS_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXISTS_EXCEPTION.getCode(), BizCodeEnum.USER_EXISTS_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
