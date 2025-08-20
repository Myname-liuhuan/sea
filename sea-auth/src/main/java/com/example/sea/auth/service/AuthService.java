package com.example.sea.auth.service;

import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.common.core.result.CommonResult;

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
     * @return 登录响应，包含accessToken和refreshToken
     */
   Mono<CommonResult<LoginResponse>> authenticate(String username, String password);
    
    /**
     * 刷新token
     * @param refreshToken 刷新token
     * @return 新token
     */
    Mono<CommonResult<LoginResponse>> refreshToken(String refreshToken);
}
