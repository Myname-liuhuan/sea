package com.example.sea.workflow.api.param;

import com.example.sea.common.core.entity.param.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单查询通用参数。
 *
 * <p>用于 {@code /my-applications} / {@code /pending-approvals} /
 * {@code /all-tasks}。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工单查询参数")
public class WorkflowTaskQueryParam extends BaseParam {

    @Schema(description = "工单状态（可选）")
    private Integer status;

    @Schema(description = "紧急程度 1 普通 / 2 紧急（可选）")
    private Integer urgency;

    @Schema(description = "申请人 ID（仅监控页可用）")
    private Long applicantId;

    @Schema(description = "目标用户 ID")
    private Long targetUserId;
}
