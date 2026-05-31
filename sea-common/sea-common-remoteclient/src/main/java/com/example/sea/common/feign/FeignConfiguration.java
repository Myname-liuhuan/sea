package com.example.sea.common.feign;

import com.example.sea.common.feign.properties.FeignTokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeignTokenProperties.class)
public class FeignConfiguration {
}
