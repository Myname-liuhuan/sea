package com.example.sea.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * auth模块启动类
 * @author liuhuan
 * @date 2025-07-30
 */
@EnableFeignClients(basePackages = "com.example.sea.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.example.sea.auth", "com.example.sea.common"} )
public class SeaAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeaAuthApplication.class, args);
	}

}
