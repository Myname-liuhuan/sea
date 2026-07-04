package com.example.sea.system.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码入参（M5.A 内部 Feign 调用方）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "重置密码入参")
public class ResetPasswordRequest {

    @Schema(description = "新密码明文（已被 sea-workflow 的 PasswordResetDelegate 生成）")
    @NotBlank
    private String newPassword;
}
