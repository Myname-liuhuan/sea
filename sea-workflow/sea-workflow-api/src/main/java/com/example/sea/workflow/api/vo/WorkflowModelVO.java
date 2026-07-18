package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程模型视图（设计器详情页用，含 META_INFO_ 解析后字段）。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "Flowable 流程模型视图")
public class WorkflowModelVO {

    @Schema(description = "模型 ID（Flowable 生成的字符串 ID）")
    private String id;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型 Key")
    private String key;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "META_INFO_ 原始 JSON 字符串")
    private String metaInfo;

    @Schema(description = "已部署的 deploymentId（未部署时为空）")
    private String deploymentId;

    @Schema(description = "多租户 ID（本期未启用）")
    private String tenantId;

    @Schema(description = "业务类型（从 META_INFO_ 解析）")
    private String businessType;

    @Schema(description = "业务描述（从 META_INFO_ 解析）")
    private String description;

    @Schema(description = "创建人 ID（从 META_INFO_ 解析）")
    private Long creatorId;

    @Schema(description = "创建人姓名（从 META_INFO_ 解析）")
    private String creatorName;

    @Schema(description = "META_INFO_ schema 版本（用于后续演进）")
    private Integer metaVersion;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;
}