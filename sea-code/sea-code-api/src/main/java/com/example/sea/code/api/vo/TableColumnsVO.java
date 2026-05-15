package com.example.sea.code.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "表字段信息")
public class TableColumnsVO {

    @Schema(description = "列名称")
    private String columnName;

    @Schema(description = "列类型")
    private String columnType;

    @Schema(description = "列对应的实体类类型")
    private String entityType;

    @Schema(description = "列注释")
    private String columnComment;

}
