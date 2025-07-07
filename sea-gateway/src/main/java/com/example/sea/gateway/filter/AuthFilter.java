package com.example.sea.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import com.example.sea.gateway.properties.SecurityWhiteListProperties;

import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
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
    /** 认证jwt在header中的key */
    private static final String AUTH_HEADER = "Authorization";
    /** 认证jwt的前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    /** 验证不通过的提示 */
    private static final String UNAUTHORIZED_MESSAGE_TEMPLATE = "{\"code\":401,\"message\":\"%s\"}";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 读取配置白名单，支持 Ant 路径匹配
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTH_HEADER);
        if (Objects.isNull(authHeader) || StringUtils.isEmpty(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.error("认证信息缺失");
            return unauthorized(exchange);
        }

        String token = authHeader.replace(TOKEN_PREFIX, "");
        try {
            //从环境变量获取secretKey
            String secretKey = System.getenv("SECRET_KEY");
            if (StringUtils.isEmpty(secretKey)) {
                log.error("环境变量 SECRET_KEY 未设置");
                return unauthorized(exchange);
            }
                
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("userId", claims.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("认证失败token:{}", token, e);
            return unauthorized(exchange);
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
