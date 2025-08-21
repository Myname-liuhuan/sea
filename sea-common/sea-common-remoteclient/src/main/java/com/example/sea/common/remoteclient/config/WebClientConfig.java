package com.example.sea.common.remoteclient.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类
 * @author liuhuan
 * @date 2025-08-18
 */
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
        .filter((request, next) -> {
            String token = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
            ClientRequest newRequest = ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .build();
            return next.exchange(newRequest);
        });
    }
}
