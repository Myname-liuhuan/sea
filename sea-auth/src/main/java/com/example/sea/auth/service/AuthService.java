package com.example.sea.auth.service;

import reactor.core.publisher.Mono;

/**
 * 认证服务接口
 * @author liuhuan
 * @date 2025-08-04
 */
public interface AuthService {
    
    /**
     * 用户认证
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    Mono<String> authenticate(String username, String password);
    
    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 新token
     */
    Mono<String> refreshToken(String refreshToken);
}
