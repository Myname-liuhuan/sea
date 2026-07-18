package com.example.sea.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新流程模型元数据请求。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "更新流程模型元数据请求")
public class UpdateModelRequest {

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "业务描述")
    private String description;

    @Schema(description = "业务类型")
    private String businessType;
}