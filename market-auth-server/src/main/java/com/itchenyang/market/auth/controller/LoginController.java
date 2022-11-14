package com.itchenyang.market.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.itchenyang.common.constant.AuthServerConstant;
import com.itchenyang.common.exception.BizCodeEnum;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.auth.feign.MemberFeignService;
import com.itchenyang.market.auth.feign.ThirdPartyFeignService;
import com.itchenyang.common.vo.MemberRespVo;
import com.itchenyang.market.auth.vo.UserLoginVo;
import com.itchenyang.market.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BigKel
 * @createTime 2022/11/5
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {

        // TODO 接口防刷


        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (redisCode != null && System.currentTimeMillis() - Long.parseLong(redisCode.split("_")[1]) < 60000) {
            return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
        }

        // 验证码的再次校验   key: phone   value: code
//        String code = UUID.randomUUID().toString().substring(0, 5);
        String code = "root";
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                code + "_" + System.currentTimeMillis(),
                10, TimeUnit.MINUTES);

        thirdPartyFeignService.sendCode(phone, code);

        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.bigkel.com/register.html";
        }

        // 验证码校验
        String code = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(code)) {
            if (vo.getCode().equals(code.split("_")[0])) {
                // 删除redis   验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

                // 调用member服务进行注册
                try {
                    R r = memberFeignService.regist(vo);
                    if (r.getCode() == 0) {
                        return "redirect:http://auth.bigkel.com/login.html";
                    } else {
                        Map<String, String> errors = new HashMap<>();
                        errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
                        redirectAttributes.addFlashAttribute("errors", errors);
                        return "redirect:http://auth.bigkel.com/register.html";
                    }
                } catch (Exception e) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", "远程服务异常");
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.bigkel.com/register.html";
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.bigkel.com/register.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.bigkel.com/register.html";
        }
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            return "redirect:http://bigkel.com";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        // 调用远程接口登录
        try {
            R r = memberFeignService.login(vo);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                return "redirect:http://bigkel.com";
            } else {
                HashMap<String, String> errors = new HashMap<>();
                errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.bigkel.com/login.html";
            }
        } catch (Exception e) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg", "服务异常，请稍后再试");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.bigkel.com/login.html";
        }
    }
}
