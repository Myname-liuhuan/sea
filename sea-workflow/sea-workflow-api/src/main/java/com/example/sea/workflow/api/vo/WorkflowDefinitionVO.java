package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程定义视图。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "流程定义视图")
public class WorkflowDefinitionVO {

    private Long id;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "节点 / 审批人解析器 JSON")
    private String definitionJson;

    @Schema(description = "0 停用 1 启用")
    private Integer enabled;

    @Schema(description = "是否当前启用版本")
    private Integer isCurrent;
}
