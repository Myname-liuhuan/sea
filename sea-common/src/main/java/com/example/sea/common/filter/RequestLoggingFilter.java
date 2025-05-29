package com.example.sea.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 打印每次请求路径
 * @author liuhuan
 * @date 2025-05-29
 */
@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();
        
        logger.info("Request received - Method: {}, Path: {}", method, path);
        
        return chain.filter(exchange)
            .doOnSuccess(v -> logger.info("Request completed - Method: {}, Path: {}", method, path))
            .doOnError(e -> logger.error("Request failed - Method: {}, Path: {}, Error: {}", 
                method, path, e.getMessage()));
    }
}
