package com.example.sea.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.example.sea.gateway", "com.example.sea.common"} )
public class SeaGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaGatewayApplication.class, args);
    }

}
