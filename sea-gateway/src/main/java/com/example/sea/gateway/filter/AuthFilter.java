package com.example.sea.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.example.sea.gateway.properties.SecurityWhiteListProperties;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 网关认证过滤器
 * 1，如果网关和服务间不是完全信任的，网关传递的用户信息不能完全信任
 *    所以网关只做轻量化校验拦截一些明显错误的token，完整的校验放到服务中做
 * 2， 如果网关和服务间是完全信任的，可以在网关做完整的校验，服务直接使用网关传递的用户信息
 * 当前是情况1
 * @author liuhuan
 * @date 2025/05/30
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered  {

    private final SecurityWhiteListProperties securityWhiteListProperties;

    @Autowired
    public AuthFilter(SecurityWhiteListProperties securityWhiteListProperties) {
        this.securityWhiteListProperties = securityWhiteListProperties;
    }

    /** 认证jwt的前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    /** 验证不通过的提示 */
    private static final String UNAUTHORIZED_MESSAGE_TEMPLATE = "{\"code\":401,\"message\":\"%s\"}";
    // Base64URL 正则（只允许 A-Z a-z 0-9 - _）
    private static final Pattern BASE64URL_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单放行
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        // 读取 Authorization 头
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.error("认证信息缺失或格式不正确");
            return unauthorized(exchange);
        }

        // 只做格式检查，不解析/校验 JWT
        String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            log.error("Token 内容为空");
            return unauthorized(exchange);
        }

        if (!isJwtFormat(token)) {
            return unauthorized(exchange, "Token 格式非法");
        }

        // 保留原始 Authorization 头，转发给下游业务服务
        return chain.filter(exchange);
    }

    /**
     * 粗略判断是否是 JWT
     */
    private boolean isJwtFormat(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        // 三段都必须是 Base64URL 格式
        for (String part : parts) {
            if (!BASE64URL_PATTERN.matcher(part).matches()) {
                return false;
            }
        }

        try {
            // 第一段解码出来必须是 JSON，且包含 "alg"
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            return headerJson.contains("\"alg\"");
        } catch (IllegalArgumentException e) {
            return false; // Base64 解码失败
        }
    }


    private boolean isWhiteListed(String path) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return securityWhiteListProperties.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 验证不通过时返回未授权响应 默认提示
     * @param exchange
     * @return
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        return unauthorized(exchange, "认证失败");
    }

    /**
     * 验证不通过时返回未授权响应 自定义提示
     * @param exchange
     * @param message
     * @return
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response
                        .bufferFactory()
                        .wrap(String.format(UNAUTHORIZED_MESSAGE_TEMPLATE, message)
                                .getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
