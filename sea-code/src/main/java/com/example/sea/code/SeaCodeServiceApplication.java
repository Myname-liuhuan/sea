package com.example.sea.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @author liuhuan
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.sea.code", "com.example"})
@MapperScan("com.example.sea.code.mapper")
public class SeaCodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaCodeServiceApplication.class, args);
    }

}
