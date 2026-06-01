package com.example.sea.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.sea.system.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.sea")
@SpringBootApplication(scanBasePackages = {"com.example.sea.system", "com.example.sea.common"} )
public class SystemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemServiceApplication.class, args);
	}

}
