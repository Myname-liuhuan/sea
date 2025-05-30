package com.example.sea.gateway.filter;

import java.nio.charset.StandardCharsets;

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

import com.example.sea.gateway.properties.SecurityWhiteListProperties;

import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
 * @author liuhuan
 * @date 2025/05/30
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered  {

    private final SecurityWhiteListProperties securityWhiteListProperties;

    @Autowired
    public AuthFilter(SecurityWhiteListProperties securityWhiteListProperties) {
        this.securityWhiteListProperties = securityWhiteListProperties;
    }

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 读取配置白名单，支持 Ant 路径匹配
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTH_HEADER);
        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            return unauthorized(exchange);
        }

        String token = authHeader.replace(TOKEN_PREFIX, "");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("secretKey")
                    .parseClaimsJws(token)
                    .getBody();

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("userId", claims.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private boolean isWhiteListed(String path) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return securityWhiteListProperties.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"Unauthorized\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
