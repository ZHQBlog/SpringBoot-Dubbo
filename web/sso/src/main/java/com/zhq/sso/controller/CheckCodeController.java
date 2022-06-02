package com.zhq.sso.controller;

import com.zhq.api.vo.ImageCode;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;


@Controller
public class CheckCodeController {
    /**
     * 登录生成验证码
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/imagecode")
    public void loginImageCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream os = response.getOutputStream();
        Map<String,Object> map = ImageCode.getImageCode(100, 30);

        request.getSession().setAttribute("simpleCaptcha", map.get("strEnsure").toString().toLowerCase());
        request.getSession().setAttribute("codeTime",new Date().getTime());

        try {
            ImageIO.write((BufferedImage) map.get("image"), "JPEG", os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 判断验证
     * @param code
     * @param session
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkcode")
    @ResponseBody
    public String loginCheckCode(String code, HttpSession session) throws Exception {
        Object checkCode = session.getAttribute("simpleCaptcha") ; //验证码对象

        if(checkCode == null){
            return "验证码已失效，请重新输入！";
        }

        String captcha = checkCode.toString();
        long newTime = new Date().getTime();
        Long codeTime = Long.valueOf(session.getAttribute("codeTime")+"");

        if(StringUtils.isEmpty(code) || captcha == null ||  !(code.equalsIgnoreCase(captcha))) {
            return "验证码错误！";
        } else if ((newTime-codeTime)/1000/60>3) {
            //验证码有效时长为3分钟
            return "验证码已失效，请重新输入！";
        }else {
            return "1";
        }
    }
}
