package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程模型列表项视图（省 META_INFO_ 原始字符串）。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "流程模型列表项")
public class WorkflowModelListItemVO {

    @Schema(description = "模型 ID")
    private String id;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型 Key")
    private String key;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "已部署的 deploymentId")
    private String deploymentId;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务描述")
    private String description;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;
}