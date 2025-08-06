package com.example.sea.auth.service.impl;

import com.example.sea.auth.service.AuthService;
import com.example.sea.auth.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 认证服务实现
 * @author liuhuan
 * @date 2025-08-04
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    // 模拟用户验证，实际应该从数据库查询
    private boolean validateUser(String username, String password) {
        // TODO: 实际项目中应该从数据库验证用户
        return "admin".equals(username) && "admin123".equals(password);
    }

    @Override
    public Mono<String> authenticate(String username, String password) {
        if (validateUser(username, password)) {
            // 模拟用户角色
            List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
            String token = jwtUtil.generateToken(username, roles);
            return Mono.just(token);
        }
        return Mono.empty();
    }

    @Override
    public Mono<String> refreshToken(String refreshToken) {
        try {
            // 解析刷新token
            // TODO: 实际项目中应该验证刷新token的有效性
            String username = "admin"; // 从刷新token中获取用户名
            List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
            String newToken = jwtUtil.generateToken(username, roles);
            return Mono.just(newToken);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
