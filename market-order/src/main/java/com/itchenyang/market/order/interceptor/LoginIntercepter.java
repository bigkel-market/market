package com.itchenyang.market.order.interceptor;

import com.itchenyang.common.constant.AuthServerConstant;
import com.itchenyang.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author BigKel
 * @createTime 2022/11/30
 */
@Component
public class LoginIntercepter  implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        if (match || match1) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            // 已登录，放行
            threadLocal.set(attribute);
            return true;
        } else {
            // 进行登录
            session.setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.bigkel.com/login.html");
            return false;
        }
    }
}
