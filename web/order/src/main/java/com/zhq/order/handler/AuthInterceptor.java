package com.zhq.order.handler;

import com.zhq.api.IUserService;
import com.zhq.common.pojo.ResultBean;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @DubboReference
    private IUserService userService;

    /**
     * 判断用户是是否登录
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            //登录，则可以操作订单
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
        }
        //条状到登录页，不会向浏览器一样携带referer参数
        response.sendRedirect("http://localhost:9094/sso/login?referer="+request.getRequestURI().toString());
        return false;
    }
}
