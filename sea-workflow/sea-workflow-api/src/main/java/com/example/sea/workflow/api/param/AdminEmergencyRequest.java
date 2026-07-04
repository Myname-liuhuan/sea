package com.example.sea.workflow.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员紧急通道：绕过审批直接重置密码。
 *
 * <p>单独审计、独立接口，调用记录写到 sea-log 的 admin_audit 表（M2 仅落库，
 * 不暴露前端）；后续 admin-emergency-reset 也可考虑记录到 workflow_task
 * 但状态/角色概念不同。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "管理员紧急重置请求")
public class AdminEmergencyRequest {

    @Schema(description = "目标用户 ID")
    @NotNull
    private Long targetUserId;

    @Schema(description = "紧急操作原因（必填）")
    @NotBlank
    @Size(max = 500)
    private String reason;
}
