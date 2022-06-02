package com.zhq.sso.controller;

import com.zhq.api.IUserService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.common.utils.HttpClientUtils;
import com.zhq.entity.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("sso")
public class SSOController {

    @DubboReference
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    //登录页
    @RequestMapping("login")
    public String showLogin(HttpServletRequest request){

        //获取从哪来进入的登录页
        String referer = request.getHeader("Referer");
        if (referer==null || "".equals(referer)){
            referer = request.getParameter("referer");
        }
        request.setAttribute("referer", referer);

        return "login";
    }

    //登录认证
    @RequestMapping("checkLogin")
    public String checkLogin(@CookieValue(name = "user_cart", required = false) String userCart,
                             String username, String password, String referer,
                             HttpServletResponse response){

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User currentUser = userService.checkLogin(user);

        if (currentUser == null){
            return "login";
        }

        //生成uuid，替代jsessionid，用来判断用户是否登录
        String uuid = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("user_token", uuid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  //只能通过后端获取到该cookie信息
        //cookie.setDomain("zhq.com"); //设置父域名，所有子域名系统都可以共享，解决共享问题
        response.addCookie(cookie);

        //将凭证保存到redis
        StringBuilder redisKey = new StringBuilder("userToken::").append(uuid);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.opsForValue().set(redisKey.toString(), currentUser);
        //设置有效期
        redisTemplate.expire(redisKey.toString(), 30, TimeUnit.MINUTES);

        //登录成功之后，自动调用购物车合并接口
        //使用HttpClient来模拟发送http请求
        StringBuilder value = new StringBuilder("user_token=");
        value.append(uuid);
        value.append(";");
        value.append("user_cart=");
        value.append(userCart);

        Map<String, String> param = new HashMap<>();
        param.put("Cookie", value.toString());
        HttpClientUtils.doGetAndHeaders("http://localhost:9095/cart/merge", param);

        if (referer != null && !"".equals(referer)){
            return "redirect:"+referer;
        }

        return "redirect:http://localhost:9091/index/show";
    }

    //判断用户是否登录
    //因为其他系统也要验证是否登录，调用controller用使用httpclient，建议抽成service的方法
    @GetMapping("checkIsLogin")
    @ResponseBody
    @CrossOrigin(origins = {"http://localhost:9091", "http://localhost:9092"}, allowCredentials = "true") //解决ajax同源问题
    public ResultBean checkIsLogin(@CookieValue(name = "user_token", required = false) String uuid){

        if (uuid == null){
            return ResultBean.error("用户未登录");
        }
        return userService.checkIsLogin(uuid);
    }

    //注销
    @RequestMapping("logout")
    @ResponseBody
    public ResultBean logout(@CookieValue(name = "user_token", required = false) String uuid,
                             HttpServletResponse response){
        if (uuid == null){
            return ResultBean.error("注销失败！");
        }

        //删除cookie
        Cookie cookie = new Cookie("user_token", uuid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //cookie.setDomain("zhq.com");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        //删除redis凭证
        StringBuilder redisKey = new StringBuilder("userToken::").append(uuid);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.delete(redisKey.toString());

        return ResultBean.success("注销成功！");
    }

    //注册页
    @RequestMapping("showRegister")
    public String showRegister(){
        return "register";
    }

    //判断用户名是否存在
    @GetMapping("query")
    @ResponseBody
    public String getUser(String name){

        if (name==null || "".equals(name)){
            return "用户名不能为空！";
        }

        User user = userService.queryUser(name);
        if ("".equals(user) || null==user){
            return "";
        }else {
            return "用户名已存在！";
        }
    }

    //注册用户
    @PostMapping("/register")
    public String register(String username, String password, String email){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFlag(true);
        user.setCreateTime(new Date());
        user.setUpdateTime(user.getCreateTime());
        user.setCreateUser(1L);
        user.setUpdateUser(user.getCreateUser());

        int i = userService.insertSelective(user);
        if (i > 0) {
            return "login";
        } else {
            return "";
        }
    }
}
