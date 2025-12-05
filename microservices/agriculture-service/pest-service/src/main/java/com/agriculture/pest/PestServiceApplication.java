package com.agriculture.pest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.agriculture.pest.mapper")
public class PestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PestServiceApplication.class, args);
    }

}