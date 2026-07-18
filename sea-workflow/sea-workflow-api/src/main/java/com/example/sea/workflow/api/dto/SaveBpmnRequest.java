package com.example.sea.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 保存 BPMN XML 请求。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "保存 BPMN XML 请求")
public class SaveBpmnRequest {

    @NotBlank
    @Schema(description = "bpmn20.xml 序列化结果")
    private String xml;

    @Schema(description = "可选：SVG 预览，仅用于前端缓存")
    private String svg;
}