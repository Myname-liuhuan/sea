package com.example.sea.auth.dto;

import com.example.sea.auth.validation.GroupRefresh;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌请求 DTO
 *
 * @author liuhuan
 * @date 2026-06-19
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "刷新token不能为空", groups = GroupRefresh.class)
    private String refreshToken;
}