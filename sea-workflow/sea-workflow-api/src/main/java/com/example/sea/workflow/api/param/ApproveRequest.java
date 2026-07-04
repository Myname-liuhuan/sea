package com.example.sea.workflow.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批通过 / 拒绝。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "审批请求")
public class ApproveRequest {

    @Schema(description = "工单编号")
    @NotBlank
    private String taskNo;

    @Schema(description = "true 通过 / false 拒绝")
    @NotNull
    private Boolean approved;

    @Schema(description = "审批意见（可空）")
    @Size(max = 500)
    private String comment;
}
