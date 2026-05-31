package com.example.sea.common.feign.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

import com.example.sea.common.feign.exception.FeignTokenConfigurationException;
import com.example.sea.common.feign.properties.FeignTokenProperties;
import com.example.sea.common.security.utils.JwtUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign 请求拦截器，强制使用配置的 feign token
 * @author liuhuan
 * @date 2025-08-25
 */
@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    private final FeignTokenProperties feignTokenProperties;
    private final JwtUtil jwtUtil;

    @Autowired
    public FeignTokenInterceptor(FeignTokenProperties feignTokenProperties, JwtUtil jwtUtil) {
        this.feignTokenProperties = feignTokenProperties;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void apply(RequestTemplate template) {
        List<String> missing = feignTokenProperties.validate();
        if (!missing.isEmpty()) {
            throw new FeignTokenConfigurationException(missing);
        }

        String token = jwtUtil.generateConfigurableFeignToken(
                feignTokenProperties.getUser().getId(),
                feignTokenProperties.getUser().getName(),
                feignTokenProperties.getRoles(),
                feignTokenProperties.getAuthorities()
        );

        template.header(HttpHeaders.AUTHORIZATION, token);
    }
}