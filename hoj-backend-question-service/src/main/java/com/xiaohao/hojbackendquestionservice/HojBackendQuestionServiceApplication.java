package com.xiaohao.hojbackendquestionservice;

import com.xiaohao.hojbackendquestionservice.rabbitmq.RabbitMQSetup;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.xiaohao.hojbackendquestionservice.mapper")
@EnableDiscoveryClient
@ComponentScan("com.xiaohao")
@EnableFeignClients("com.xiaohao.hojbackendserviceclient.feignClient")
public class HojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        RabbitMQSetup.doInit();
        SpringApplication.run(HojBackendQuestionServiceApplication.class, args);
    }

}
