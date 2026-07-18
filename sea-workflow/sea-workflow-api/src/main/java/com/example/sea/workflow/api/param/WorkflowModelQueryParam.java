package com.example.sea.workflow.api.param;

import com.example.sea.common.core.entity.param.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程模型查询参数。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程模型查询参数")
public class WorkflowModelQueryParam extends BaseParam {

    @Schema(description = "模型名称（模糊匹配，可选）")
    private String name;

    @Schema(description = "模型 Key（精确匹配，可选）")
    private String key;

    @Schema(description = "分类（可选）")
    private String category;

    @Schema(description = "业务类型（可选）")
    private String businessType;
}