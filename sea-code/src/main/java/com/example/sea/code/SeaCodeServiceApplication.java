package com.example.sea.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author liuhuan
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.sea.code", "com.example"})
public class SeaCodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaCodeServiceApplication.class, args);
    }

}
