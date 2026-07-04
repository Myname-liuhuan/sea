package com.example.sea.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 重置密码请求（由 sea-workflow → sea-system 调用）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest {

    @Schema(description = "新密码明文（由 sea-workflow 的 PasswordResetDelegate 生成）")
    private String newPassword;

    @Schema(description = "是否触发首次登录强制改密")
    private Boolean requirePasswordChange;
}
