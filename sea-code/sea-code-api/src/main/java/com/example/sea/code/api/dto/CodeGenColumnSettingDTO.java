package com.example.sea.code.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代码生成列设置")
public class CodeGenColumnSettingDTO {

    @Schema(description = "列名")
    private String columnName;

    @Schema(description = "列类型")
    private String columnType;

    @Schema(description = "实体类类型")
    private String entityType;

    @Schema(description = "是否需要转化为实体类字段")
    private Boolean isEntityField;

}
