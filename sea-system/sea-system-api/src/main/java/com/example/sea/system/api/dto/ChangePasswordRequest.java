package com.example.sea.system.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户主动改密 / 强制改密（首次登录临时密码）的入参。
 *
 * @author liuhuan
 * @date 2026-07-05
 */
@Data
@Schema(description = "改密请求")
public class ChangePasswordRequest {

    @Schema(description = "当前密码（重置密码首次登录场景可空）")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank
    private String newPassword;
}
