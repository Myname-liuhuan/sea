package com.example.sea.auth.dto;

import com.example.sea.auth.validation.GroupLogin;
import com.example.sea.auth.validation.GroupRefresh;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 * @author liuhuan
 * @date 2025-08-04
 */
@Data
public class LoginRequestDTO {

    @NotBlank(message = "用户名不能为空", groups = GroupLogin.class)
    private String username;

    @NotBlank(message = "密码不能为空", groups = GroupLogin.class)
    private String password;

    @NotBlank(message = "刷新token不能为空" , groups = GroupRefresh.class)
    private String refreshToken;
}
