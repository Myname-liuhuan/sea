package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发起申请结果。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "发起申请返回结果")
public class ApplyResultVO {

    @Schema(description = "工单编号")
    private String taskNo;

    @Schema(description = "工单 ID")
    private Long taskId;
}