package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程模型历史版本响应 VO（不含 bpmn_xml 全文，前端列表用）。
 *
 * @author liuhuan
 * @date 2026-07-19
 */
@Data
@Schema(description = "流程模型历史版本")
public class WorkflowModelVersionVO {

    @Schema(description = "历史记录 id")
    private Long id;

    @Schema(description = "模型 id")
    private String modelId;

    @Schema(description = "版本号（单调递增）")
    private Integer version;

    @Schema(description = "保存说明")
    private String changeComment;

    @Schema(description = "保存人 user_id")
    private Long creatorId;

    @Schema(description = "保存人姓名")
    private String creatorName;

    @Schema(description = "保存时间")
    private LocalDateTime createTime;

    /** 是否是当前线上版本（按最近一次部署对照） */
    @Schema(description = "是否最新历史")
    private Boolean latest;
}