package com.agriculture.farm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 农田服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.agriculture.common.client")
@ComponentScan(basePackages = {"com.agriculture.farm", "com.agriculture.common"})
public class FarmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmServiceApplication.class, args);
    }
}
