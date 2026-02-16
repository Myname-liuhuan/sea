package com.example.sea.code;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * sea-code 启动类
 * @author liuhuan
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.sea.code.dao")
public class SeaCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaCodeApplication.class, args);
    }

}
