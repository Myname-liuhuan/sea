package com.example.sea.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SeaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaGatewayApplication.class, args);
    }

}
