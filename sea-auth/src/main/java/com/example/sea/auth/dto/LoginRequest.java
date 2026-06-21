package com.example.sea.auth.dto;

import com.example.sea.auth.validation.GroupLogin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO（仅用于登录）
 *
 * <p>注意：刷新令牌有独立的 {@link RefreshTokenRequest}。
 * 拆分原因：原 LoginRequestDTO 把 refreshToken 列在 required，
 * SpringDoc 生成 schema 时把不同 group 的约束合并，导致前端误以为 login 也要传 refreshToken。
 *
 * @author liuhuan
 * @date 2026-06-19
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空", groups = GroupLogin.class)
    private String username;

    @NotBlank(message = "密码不能为空", groups = GroupLogin.class)
    private String password;
}