package com.xiaohao.hojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xiaohao")
@MapperScan("com.xiaohao.hojbackenduserservice.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.xiaohao.hojbackendserviceclient.feignClient")
public class HojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HojBackendUserServiceApplication.class, args);
    }

}
