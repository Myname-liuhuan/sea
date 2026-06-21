package com.example.sea.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.auth.dto.LoginRequest;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.dto.RefreshTokenRequest;
import com.example.sea.auth.service.AuthService;
import com.example.sea.auth.validation.GroupLogin;
import com.example.sea.auth.validation.GroupRefresh;
import com.example.sea.common.core.result.CommonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 认证控制器
 * @author liuhuan
 * @date 2025-08-04
 */
@RestController
@RequestMapping("/")
@Tag(name = "认证管理", description = "用户认证相关操作接口")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码登录，返回访问令牌和刷新令牌")
    public CommonResult<LoginResponse> login(@RequestBody @Validated(GroupLogin.class) LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public CommonResult<LoginResponse> refresh(@RequestBody @Validated(GroupRefresh.class) RefreshTokenRequest refreshRequest) {
        return authService.refreshToken(refreshRequest.getRefreshToken());
    }
}
