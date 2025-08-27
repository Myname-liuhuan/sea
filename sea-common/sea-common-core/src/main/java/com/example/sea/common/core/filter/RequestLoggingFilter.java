package com.example.sea.common.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 打印每次请求路径
 * @author liuhuan
 * @date 2025-05-29
 */
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        logger.info("Request received - Method: {}, Path: {}", method, path);
        
        try {
            chain.doFilter(request, response);
            // logger.info("Request completed - Method: {}, Path: {}", method, path);
        } catch (Exception e) {
            logger.error("Request failed - Method: {}, Path: {}, Error: {}", 
                method, path, e.getMessage());
            throw e;
        }
    }
}
