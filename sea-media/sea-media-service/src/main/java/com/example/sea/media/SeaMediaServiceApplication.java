package com.example.sea.media;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.example.sea.media.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class SeaMediaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeaMediaServiceApplication.class, args);
	}

}
