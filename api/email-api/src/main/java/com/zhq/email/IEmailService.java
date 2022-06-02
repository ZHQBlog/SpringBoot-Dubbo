package com.zhq.email;

/**
 * 邮箱接口
 */
public interface IEmailService {
    String sendMail(String to, String verifyCode);
    Object verifyMail(String verifyCode);
}
