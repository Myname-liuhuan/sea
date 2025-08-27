package com.example.sea.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.auth.dto.LoginRequest;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.service.AuthService;
import com.example.sea.common.core.result.CommonResult;

/**
 * 认证控制器
 * @author liuhuan
 * @date 2025-08-04
 */
@RestController
@RequestMapping("/")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public CommonResult<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public CommonResult<LoginResponse> refresh(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);  
    }
}
