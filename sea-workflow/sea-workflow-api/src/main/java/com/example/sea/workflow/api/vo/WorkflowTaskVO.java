package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单视图。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "工单视图")
public class WorkflowTaskVO {

    private Long id;

    @Schema(description = "工单编号")
    private String taskNo;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "申请人 user_id")
    private Long applicantId;

    @Schema(description = "申请人姓名（脱敏展示）")
    private String applicantName;

    @Schema(description = "重置目标 user_id")
    private Long targetUserId;

    @Schema(description = "目标用户姓名")
    private String targetUserName;

    @Schema(description = "申请原因")
    private String reason;

    @Schema(description = "紧急程度 1 普通 / 2 紧急")
    private Integer urgency;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusLabel;

    @Schema(description = "当前等待节点")
    private String currentNode;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
