package com.example.sea.auth.controller;

import com.example.sea.auth.dto.LoginRequest;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.service.AuthService;
import com.example.sea.common.core.result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 认证控制器
 * @author liuhuan
 * @date 2025-08-04
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Mono<CommonResult<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Mono<CommonResult<LoginResponse>> refresh(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
                // .map(tokenResult -> {
                //     if (tokenResult.isSuccess()) {
                //         return CommonResult.success(new LoginResponse(tokenResult.getData(), null, 3600000L));
                //     } else {
                //         return CommonResult.failed(tokenResult.getMessage());
                //     }
                // });
    }
}
