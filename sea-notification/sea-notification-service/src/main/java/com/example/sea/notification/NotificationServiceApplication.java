package com.example.sea.notification;

import com.example.sea.notification.config.NotificationChannelProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.sea.notification.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.sea")
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(NotificationChannelProperties.class)
@SpringBootApplication(scanBasePackages = {"com.example.sea.notification", "com.example.sea.common"})
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
