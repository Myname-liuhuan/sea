package com.example.sea.code.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "代码生成参数")
public class CodeGenerateDTO {

    @NotNull(message = "数据源ID不能为空")
    @Schema(description = "数据源ID")
    private Long dataSourceId;

    @NotBlank(message = "数据库名称不能为空")
    @Schema(description = "数据库名称")
    private String dbName;

    @NotBlank(message = "表名不能为空")
    @Schema(description = "表名")
    private String tableName;

    @NotBlank(message = "待生成的包名不能为空")
    @Schema(description = "生成的代码包名")
    private String packageName;

    @Schema(description = "表中字段自定义规则")
    private List<CodeGenColumnSettingDTO> columnSettingList;

}
