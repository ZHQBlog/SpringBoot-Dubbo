package com.zhq.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("order")
public class OrderController {

    @RequestMapping("confirm")
    public String confirm(){
        //获取当前登录用户，地址信息
        //物流配送，支付方式
        //获取购物车信息

        return "order_confirm";
    }
}
