package com.example.sea.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 新建流程模型请求。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "新建流程模型请求")
public class CreateModelRequest {

    @NotBlank
    @Schema(description = "模型名称", example = "请假流程")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$",
             message = "key 必须以字母开头，仅含字母数字下划线")
    @Schema(description = "模型 Key（字母开头，字母数字下划线）", example = "leave")
    private String key;

    @Schema(description = "分类（如 hr / finance）")
    private String category;

    @Schema(description = "业务描述（写入 META_INFO_）")
    private String description;

    @NotBlank
    @Schema(description = "业务类型（写入 META_INFO_，用于未来与业务侧绑定）",
            example = "LEAVE")
    private String businessType;
}