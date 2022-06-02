package com.zhq.cart.handler;

import com.zhq.api.IUserService;
import com.zhq.common.pojo.ResultBean;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @DubboReference
    private IUserService userService;

    /**
     * 判断用户是是否登录，跳转到登录时的购物车
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return true;
        }

        for (Cookie cookie : cookies) {
            if ("user_token".equals(cookie.getName())) {
                String uuid = cookie.getValue();
                ResultBean resultBean = userService.checkIsLogin(uuid);
                if (resultBean.getStatusCode() == 200){
                    request.setAttribute("user", resultBean.getData());
                    return true;
                }
            }
        }
        return true;
    }
}
