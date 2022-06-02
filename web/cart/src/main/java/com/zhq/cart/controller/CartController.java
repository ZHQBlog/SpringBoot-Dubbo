package com.zhq.cart.controller;

import com.zhq.api.ICartService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@RequestMapping("cart")
public class CartController {

    @DubboReference
    private ICartService cartService;

    //添加cookie
    private void flushCookie(String uuid, HttpServletResponse response) {
        Cookie cookie = new Cookie("user_cart", uuid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30*24*60*60);
        response.addCookie(cookie);
    }

    @RequestMapping("add/{productId}/{count}")
    @ResponseBody
    public ResultBean add(@PathVariable("productId") Long productId,
                          @PathVariable("count") Integer count,
                          @CookieValue(name = "user_cart", required = false) String uuid,
                          HttpServletResponse response,
                          HttpServletRequest request){

        //查看当前用户的登录状态
        User user = (User) request.getAttribute("user");
        String key = "";
        if (user != null){
            key = new StringBuilder("user_cart:").append(user.getId()).toString();
        }else {
            if (uuid == null || "".equals(uuid)){
                uuid = UUID.randomUUID().toString();
            }
            key = new StringBuilder("user_cart:").append(uuid).toString();
        }

        //使用Ctrl + Alt + M，将代码抽成一个方法
        flushCookie(uuid, response);

        return cartService.add(key, productId, count);
    }

    @RequestMapping("list")
    @ResponseBody
    public ResultBean lsit(@CookieValue(name = "user_cart", required = false) String uuid,
                           HttpServletResponse response, HttpServletRequest request){

        User user = (User) request.getAttribute("user");
        String key = "";
        if (user != null){
            key = new StringBuilder("user_cart:").append(user.getId()).toString();
        }else {
            if (uuid == null || "".equals(uuid)){
                return ResultBean.error("购物车暂无商品信息！");
            }
            key = new StringBuilder("user_cart:").append(uuid).toString();
        }

        flushCookie(uuid, response);

        return cartService.list(key);
    }

    @RequestMapping("update/{productId}/{count}")
    @ResponseBody
    public ResultBean update(@PathVariable("productId") Long productId,
                             @PathVariable("count") Integer count,
                             @CookieValue(name = "user_cart", required = false) String uuid,
                             HttpServletResponse response,
                             HttpServletRequest request){

        User user = (User) request.getAttribute("user");
        String key = "";
        if (user != null){
            key = new StringBuilder("user_cart:").append(user.getId()).toString();
        }else {
            if (uuid == null || "".equals(uuid)){
                return ResultBean.error("更新失败！");
            }
            key = new StringBuilder("user_cart:").append(uuid).toString();
        }
        //使用Ctrl + Alt + M，将代码抽成一个方法
        flushCookie(uuid, response);

        return cartService.updateCount(key, productId, count);
    }

    @RequestMapping("del/{productId}")
    @ResponseBody
    public ResultBean del(@PathVariable("productId") Long productId,
                          @CookieValue(name = "user_cart", required = false) String uuid,
                          HttpServletResponse response,
                          HttpServletRequest request){

        User user = (User) request.getAttribute("user");
        String key = "";
        if (user != null){
            key = new StringBuilder("user_cart:").append(user.getId()).toString();
        }else {
            if (uuid == null || "".equals(uuid)){
                return ResultBean.error("删除失败！");
            }
            key = new StringBuilder("user_cart:").append(uuid).toString();
        }
        flushCookie(uuid, response);

        return cartService.del(key, productId);
    }

    @RequestMapping("merge")
    @ResponseBody
    public ResultBean merge(@CookieValue(name = "user_cart", required = false) String uuid,
                            HttpServletResponse response,
                            HttpServletRequest request){

        User user = (User) request.getAttribute("user");
        if (user == null){
            return ResultBean.error("未登录");
        }
        if (uuid == null || "".equals(uuid)){
            return ResultBean.error("不存在为登录购物车！");
        }

        String noLoginkey = new StringBuilder("user_cart:").append(uuid).toString();
        String loginkey = new StringBuilder("user_cart:").append(user.getId()).toString();

        //删除cookie
        Cookie cookie = new Cookie("user_cart", uuid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return cartService.merge(noLoginkey, loginkey);
    }
}
