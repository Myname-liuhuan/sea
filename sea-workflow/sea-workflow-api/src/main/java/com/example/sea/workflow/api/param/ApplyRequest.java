package com.example.sea.workflow.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发起重置密码申请。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "发起重置密码申请请求")
public class ApplyRequest {

    @Schema(description = "重置目标用户 ID")
    @NotNull
    private Long targetUserId;

    @Schema(description = "申请原因")
    @NotBlank
    @Size(max = 500)
    private String reason;

    @Schema(description = "紧急程度 1 普通 / 2 紧急", allowableValues = {"1", "2"})
    @NotNull
    private Integer urgency;
}
