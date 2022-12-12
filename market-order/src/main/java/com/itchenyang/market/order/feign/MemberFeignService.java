package com.itchenyang.market.order.feign;

import com.itchenyang.market.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/12/3
 */
@FeignClient("market-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<MemberAddressVo> getCurrentUserAddress(@PathVariable("memberId") Long memberId);
}
