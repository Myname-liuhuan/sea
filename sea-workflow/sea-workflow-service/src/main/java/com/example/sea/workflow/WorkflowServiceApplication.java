package com.example.sea.workflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.sea.workflow.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.sea")
@SpringBootApplication(scanBasePackages = {"com.example.sea.workflow", "com.example.sea.common"})
public class WorkflowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowServiceApplication.class, args);
    }
}
