package com.zhq.backgroud;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDubbo //dubbo分布式
@Import(FdfsClientConfig.class) //Fastdfs文件服务器
public class BackgroudApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackgroudApplication.class, args);
    }

}
