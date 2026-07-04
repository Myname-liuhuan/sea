package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批记录视图。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "审批记录视图")
public class WorkflowApprovalVO {

    private Long id;

    @Schema(description = "工单 ID")
    private Long taskId;

    @Schema(description = "节点标识")
    private String nodeKey;

    @Schema(description = "审批层级")
    private Integer nodeOrder;

    @Schema(description = "审批人 user_id")
    private Long approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "1 通过 0 拒绝")
    private Integer approved;

    @Schema(description = "意见")
    private String comment;

    @Schema(description = "转交来源 user_id")
    private Long delegatedFrom;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}
