package com.example.sea.auth.controller;

import com.example.sea.auth.dto.LoginRequest;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(token -> ResponseEntity.ok(new LoginResponse(token)))
                .defaultIfEmpty(ResponseEntity.status(401).build());
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<LoginResponse>> refresh(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken)
                .map(token -> ResponseEntity.ok(new LoginResponse(token)))
                .defaultIfEmpty(ResponseEntity.status(401).build());
    }
}
