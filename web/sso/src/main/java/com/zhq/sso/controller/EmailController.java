package com.zhq.sso.controller;

import com.zhq.email.IEmailService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@Controller
@RequestMapping("code")
public class EmailController {

    @DubboReference
    private IEmailService emailService;

    @GetMapping("emailCode")
    @ResponseBody
    public void emailCode(String email){

        if (email == null || "".equals(email)){
            return;
        }
        //取随机产生的码
        StringBuilder strEnsure = new StringBuilder();
        Random random = new Random();
        //生成6位十以内的数字
        for (int i = 0; i < 6; ++i) {
            int index = random.nextInt(10);
            strEnsure.append(index);
        }
        String code = strEnsure.toString();

        emailService.sendMail(email, code);
    }

    @GetMapping("verifyEmailCode")
    @ResponseBody
    public String verifyEmailCode(String code){

        Object verifyMail = emailService.verifyMail(code);

        if(verifyMail == null){
            return "验证码错误或已失效！";
        } else {
            return "1";
        }
    }
}
