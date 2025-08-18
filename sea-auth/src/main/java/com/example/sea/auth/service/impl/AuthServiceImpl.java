package com.example.sea.auth.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.sea.auth.service.AuthService;
import com.example.sea.auth.webclient.SystemWebClient;
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
    private final SystemWebClient systemWebClient;

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    @Autowired
    public AuthServiceImpl(JwtUtil jwtUtil, SystemWebClient systemWebClient) {
        this.jwtUtil = jwtUtil;
        this.systemWebClient = systemWebClient;
    }

    // 从远程系统服务验证用户
    private Mono<LoginUser> validateUser(String username, String password) {
        return systemWebClient.getLoginUser(username)
                .flatMap(result -> {
                    if (result != null && result.isSuccess() && result.getData() != null 
                                                                    && StringUtils.isNotBlank(password)) {
                        LoginUser loginUser = result.getData();
                        // 这里应该添加密码验证逻辑
                        String passwordHash =  bCryptPasswordEncoder.encode(password);
                        if (Objects.equals(loginUser.getPassword(), passwordHash)) {
                            return Mono.just(loginUser);
                        }
                    }
                    return Mono.empty();
                });
    }

    /**
     * 用户登录，生成 JWT 令牌
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌AccessToken
     */
    @Override
    public Mono<String> authenticate(String username, String password) {
        return validateUser(username, password)
                .map(loginUser -> jwtUtil.generateAccessToken(loginUser));
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
            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            
            return validateUser(username, password)
                    .map(loginUser -> jwtUtil.generateAccessToken(loginUser));
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
