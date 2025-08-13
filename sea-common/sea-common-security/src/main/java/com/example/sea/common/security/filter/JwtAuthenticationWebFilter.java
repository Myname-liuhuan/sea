package com.example.sea.common.security.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.sea.common.security.properties.SecurityWhiteListProperties;
import com.example.sea.common.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

    private final SecurityWhiteListProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationWebFilter(SecurityWhiteListProperties securityProperties, JwtUtil jwtUtil) {
        this.securityProperties = securityProperties;
        this.jwtUtil = jwtUtil;
    }

     /** 验证不通过的提示 */
    private static final String UNAUTHORIZED_MESSAGE_TEMPLATE = "{\"code\":401,\"message\":\"%s\"}";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 白名单直接放行
        for (String pattern : securityProperties.getWhitelist()) {
            if (pathMatcher.match(pattern, path)) {
                return chain.filter(exchange);
            }
        }
       
        String token = resolveToken(exchange);
        if (token == null) {
            return unauthorized(exchange, "token缺失");
        }

        try {
             Claims claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
             log.error("认证失败token:{}", token, e);
             return unauthorized(exchange, "无效的token");
        }
      
        // 设置认证上下文
        Authentication authentication = getAuthentication(token);
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    /**
     * 提取token
     * @param exchange
     * @return
     */
    private String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.parseToken(token);
        String username = claims.getSubject();
        Object authoritiesObj = claims.get("authorities");
        String authoritiesStr = authoritiesObj != null ? authoritiesObj.toString() : "";
        // authoritiesStr 可能是逗号分隔的字符串
        String[] authoritiesArr = authoritiesStr.split(",");
        UserDetails userDetails = User.withUsername(username)
                .password("") // 密码可为空
                .authorities(authoritiesArr)
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
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
}
