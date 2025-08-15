package com.example.sea.auth.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sea.auth.feign.SystemFeignClient;
import com.example.sea.auth.service.AuthService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

/**
 * 认证服务实现
 * @author liuhuan
 * @date 2025-08-04
 */
@Service
public class AuthServiceImpl implements AuthService {

    
    private final JwtUtil jwtUtil;
    private final SystemFeignClient systemFeignClient;

    @Autowired
    public AuthServiceImpl(JwtUtil jwtUtil, SystemFeignClient systemFeignClient) {
        this.jwtUtil = jwtUtil;
        this.systemFeignClient = systemFeignClient;
    }

    // 从远程系统服务验证用户
    private LoginUser validateUser(String username, String password) {
        CommonResult<LoginUser> result = systemFeignClient.getLoginUser(username);
        if (result.isSuccess() && result.getData() != null) {
            LoginUser loginUser = result.getData();
            // 这里应该添加密码验证逻辑
            if (loginUser.getPassword() != null && loginUser.getPassword().equals(password)) {
                return loginUser;
            }
        }
        return null;
    }

    /**
     * 用户登录，生成 JWT 令牌
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌AccessToken
     */
    @Override
    public Mono<String> authenticate(String username, String password) {
        LoginUser loginUser = validateUser(username, password);
        if (loginUser != null) {
            String token = jwtUtil.generateAccessToken(loginUser);
            return Mono.just(token);
        }
        return Mono.empty();
    }

    /**
     * 通过 refreshToken 刷新 AccessToken
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌AccessToken
     */
    @Override
    public Mono<String> refreshToken(String refreshToken) {
        try {
            //先判断是不是refreshToken
            if(!jwtUtil.isRefreshToken(refreshToken)){
                return Mono.empty();
            }
            // 解析刷新token
            Claims claims =  jwtUtil.parseToken(refreshToken);
            LoginUser loginUser = validateUser(
                (String)claims.get("username"), (String) claims.get("password")
            );
            String newAccessToken = jwtUtil.generateAccessToken(loginUser);
            return Mono.just(newAccessToken);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
