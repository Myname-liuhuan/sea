package com.example.sea.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.service.AuthService;
import com.example.sea.auth.webclient.SystemWebClient;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.JwtRedisUtil;
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
    private final JwtRedisUtil jwtRedisUtil;
    private final SystemWebClient systemWebClient;

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    @Autowired
    public AuthServiceImpl(JwtUtil jwtUtil, JwtRedisUtil jwtRedisUtil, SystemWebClient systemWebClient) {
        this.jwtUtil = jwtUtil;
        this.jwtRedisUtil = jwtRedisUtil;
        this.systemWebClient = systemWebClient;
    }

    /**
     * 用户登录，生成 JWT 令牌
     * @param username 用户名
     * @param password 密码
     * @return 登录响应，包含accessToken和refreshToken
     */
    @Override
    public Mono<CommonResult<LoginResponse>> authenticate(String username, String password) {
        return validateUser(username, password)
                .flatMap(result -> {
                    if (result.isSuccess()) {
                        LoginUser loginUser = result.getData();
                        String accessToken = jwtUtil.generateAccessToken(loginUser);
                        String refreshToken = jwtUtil.generateRefreshToken(loginUser);
                        Long expiresIn = jwtUtil.getAccessTokenExpirationMs();
                        
                        // 存储token到Redis
                        jwtRedisUtil.storeToken(refreshToken, loginUser.getId(), jwtUtil.getRefreshTokenExpirationMs());
                        
                        return Mono.just(CommonResult.success(new LoginResponse(accessToken, refreshToken, expiresIn)));
                    } else {
                        // 保留原始的错误信息
                        return Mono.just(CommonResult.failed(result.getMessage()));
                    }
                });
    }

    /**
     * 通过 refreshToken 刷新 AccessToken
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌AccessToken
     */
    @Override
    public Mono<CommonResult<LoginResponse>> refreshToken(String refreshToken) {
        try {
            // 先验证token是否存在于Redis中
            if (!jwtRedisUtil.validateToken(refreshToken)) {
                return Mono.just(CommonResult.failed("刷新token无效或已过期"));
            }
            
            // 先判断是不是refreshToken
            if(!jwtUtil.isRefreshToken(refreshToken)){
                return Mono.just(CommonResult.failed("无效的刷新token"));
            }
            
            // 解析刷新token
            Claims claims =  jwtUtil.parseToken(refreshToken);
            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            
            return validateUser(username, password)
                    .flatMap(result -> {
                        if (result.isSuccess()) {
                            LoginUser loginUser = result.getData();
                            String newAccessToken = jwtUtil.generateAccessToken(loginUser);
                            return Mono.just(CommonResult.success(new LoginResponse(newAccessToken, refreshToken, jwtUtil.getAccessTokenExpirationMs())));
                        } else {
                            return Mono.just(CommonResult.failed(result.getMessage()));
                        }
                    });
        } catch (Exception e) {
            return Mono.just(CommonResult.failed("刷新token解析失败"));
        }
    }

    /**
     * 从远程系统服务验证用户
     * @param username
     * @param password
     * @return 成功返回登录用户信息，失败返回包含错误信息的CommonResult
     */
    private Mono<CommonResult<LoginUser>> validateUser(String username, String password) {
        return systemWebClient.getLoginUser(username)
                .flatMap(result -> {
                    if (result != null && result.isSuccess() && result.getData() != null 
                              && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(result.getData().getPassword())) {
                        LoginUser loginUser = result.getData();
                        // 密码验证逻辑
                        if (bCryptPasswordEncoder.matches(password, loginUser.getPassword())) {
                            return Mono.just(CommonResult.success(loginUser));
                        } else {
                            return Mono.just(CommonResult.failed("密码错误"));
                        }
                    } else if (result != null && !result.isSuccess()) {
                        // 保留远程服务返回的错误信息
                        return Mono.just(CommonResult.failed(result.getMessage()));
                    } else {
                        // 其他情况，如用户不存在或数据不完整
                        return Mono.just(CommonResult.failed("用户不存在或数据不完整"));
                    }
                });
    }
}
