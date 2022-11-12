package com.itchenyang.market.auth.feign;

import com.itchenyang.common.utils.R;
import com.itchenyang.market.auth.vo.UserLoginVo;
import com.itchenyang.market.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author BigKel
 * @createTime 2022/11/7
 */
@FeignClient("market-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
