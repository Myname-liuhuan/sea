package com.example.sea.auth.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;

import reactor.core.publisher.Mono;

/**
 * 访问系统模块的服务间调用客户端
 * @author liuhuan
 * @date 2025-08-18
 */
@Component
public class SystemWebClient {

    private final WebClient webClient;

    public SystemWebClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://sea-system")
                .build();
    }

    public Mono<CommonResult<LoginUser>> getLoginUser(String username) {
        return webClient.get()
                .uri("/sysUser/getLoginUser?username={username}", username)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResult<LoginUser>>() {});
    }
}
