package com.xiaohao.hojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.xiaohao")
@EnableFeignClients("com.xiaohao.hojbackendserviceclient.feignClient")
public class HojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HojBackendJudgeServiceApplication.class, args);
    }

}
