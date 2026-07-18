package com.example.sea.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批人转交 / 委派。
 *
 * <p>M2 核心三项之一。审批人把自己当前的待办转交给他人后，
 * Flowable 把对应 userTask 的 assignee 替换为 toUserId，并写
 * workflow_approval.delegated_from = 当前审批人。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "审批人转交请求")
public class ReassignRequest {

    @Schema(description = "工单编号")
    @NotBlank
    private String taskNo;

    @Schema(description = "被转交给的目标用户 ID")
    @NotNull
    private Long toUserId;

    @Schema(description = "转交说明（可空）")
    @Size(max = 500)
    private String comment;
}
