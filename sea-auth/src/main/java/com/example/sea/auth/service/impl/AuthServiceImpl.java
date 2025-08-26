package com.example.sea.auth.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.feign.SystemFeignClient;
import com.example.sea.auth.service.AuthService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.JwtRedisUtil;
import com.example.sea.common.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务实现
 * @author liuhuan
 * @date 2025-08-04
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final JwtRedisUtil jwtRedisUtil;
    private final SystemFeignClient systemFeignClient;

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    @Autowired
    public AuthServiceImpl(JwtUtil jwtUtil, JwtRedisUtil jwtRedisUtil, SystemFeignClient systemFeignClient) {
        this.jwtUtil = jwtUtil;
        this.jwtRedisUtil = jwtRedisUtil;
        this.systemFeignClient = systemFeignClient;
    }

    /**
     * 用户登录，生成 JWT 令牌
     * @param username 用户名
     * @param password 密码
     * @return 登录响应，包含accessToken和refreshToken
     */
    @Override
    public CommonResult<LoginResponse> authenticate(String username, String password) {
        CommonResult<LoginUser> vaResult = validateUser(username, password);
        if (!vaResult.isSuccess()) {
            return CommonResult.failed(vaResult.getMessage());
        }

        LoginUser loginUser = vaResult.getData();
        //设置当前token的版本
        loginUser.setVersion(jwtRedisUtil.getUserVersion(String.valueOf(loginUser.getId())));
        //生成token
        String accessToken = jwtUtil.generateAccessToken(loginUser);
        String refreshToken = jwtUtil.generateRefreshToken(loginUser);
        Long expiresIn = jwtUtil.getAccessTokenExpirationMs();
        
        // 存储token到Redis
        jwtRedisUtil.storeToken(refreshToken, loginUser.getId(), jwtUtil.getRefreshTokenExpirationMs());
        
        return CommonResult.success(new LoginResponse(accessToken, refreshToken, expiresIn));
    }

    /**
     * 通过 refreshToken 刷新 AccessToken
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌AccessToken
     */
    @Override
    public CommonResult<LoginResponse> refreshToken(String refreshToken) {
        // 先验证token是否存在于Redis中
        if (!jwtRedisUtil.validateToken(refreshToken)) {
            return CommonResult.failed("刷新token无效或已过期");
        }
        
        // 先判断是不是refreshToken
        if(!jwtUtil.isRefreshToken(refreshToken)){
            return CommonResult.failed("无效的刷新token");
        }
        
        // 解析刷新token
        Claims claims =  jwtUtil.parseToken(refreshToken);
        String username = (String) claims.get("username");
        String password = (String) claims.get("password");
        
        CommonResult<LoginUser> vaResult = validateUser(username, password);
        if (!vaResult.isSuccess()) {
            return CommonResult.failed(vaResult.getMessage());
        }
        String newAccessToken = jwtUtil.generateAccessToken(vaResult.getData());
        return CommonResult.success(new LoginResponse(newAccessToken, refreshToken, jwtUtil.getAccessTokenExpirationMs()));   
    }

    /**
     * 从远程系统服务验证用户
     * @param username
     * @param password
     * @return 成功返回登录用户信息，失败返回包含错误信息的CommonResult
     */
    private CommonResult<LoginUser> validateUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return CommonResult.failed("用户名或密码不能为空");
        }

        CommonResult<LoginUser> remoteResult = systemFeignClient.getLoginUser(username);
        if (Objects.isNull(remoteResult) || !remoteResult.isSuccess()) {
            String msg = Objects.nonNull(remoteResult) ? remoteResult.getMessage() : "未知错误";
            log.error("获取用户【{}】信息失败，错误 {}", username, msg);
            return CommonResult.failed(msg);
        }

        if(StringUtils.isBlank(remoteResult.getData().getPassword()) 
                || !bCryptPasswordEncoder.matches(password, remoteResult.getData().getPassword())){
            return CommonResult.failed("用户名或密码错误");
        }
        return remoteResult;
    }
}
