package com.zhq.emailservice.service;

import com.zhq.email.IEmailService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@DubboService
public class EmailService implements IEmailService {

    @Autowired(required = false) //required将检测级别降低，防止爆红
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    //邮件发件人
    @Value("${mail.fromMail.user}")
    private String from;

    //发送邮件
    @Override
    public String sendMail(String to, String verifyCode) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件发送者
        message.setFrom(from);
        //message.setCc(from);
        // 设置邮件接收者
        message.setTo(to);
        // 设置邮件的主题
        message.setSubject("注册验证码");

        String text = "您的验证码为：" + verifyCode + "，请勿泄露给他人。";
        message.setText(text);
        try {
            //存入redis设置3分钟后失效，实现验证码3分钟的有效
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.opsForValue().set("code::"+verifyCode, verifyCode);
            redisTemplate.expire("code::"+verifyCode, 3, TimeUnit.MINUTES);
            // 发送邮件
            javaMailSender.send(message);
            return "success";
        } catch (MailException e) {
            e.printStackTrace();
            return "error";
        }
    }


    //校验验证码
    @Override
    public Object verifyMail(String verifyCode) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        Object result = redisTemplate.opsForValue().get("code::" + verifyCode);
        return result;
    }
}
