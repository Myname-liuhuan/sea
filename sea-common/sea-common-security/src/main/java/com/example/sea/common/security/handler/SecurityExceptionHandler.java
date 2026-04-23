package com.example.sea.common.security.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全模块全局异常处理
 * 因为这里设置的@Order值比较小所以是优先命中的，所以注意只写必须德尔不要写多余的
 * @author liuhuan
 * @date 2026-04-23
 */
@RestControllerAdvice
@Order(1)
public class SecurityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    /**
     * 处理权限不足异常（@Permission注解拒绝访问）
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Map<String, Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied - Path: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), ex.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", "无权限访问");
        result.put("data", null);
        return result;
    }
}
