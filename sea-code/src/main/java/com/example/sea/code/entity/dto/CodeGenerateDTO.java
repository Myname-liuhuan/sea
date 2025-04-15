package com.example.sea.code.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 代码生成接口入参
 * @author liuhuan
 * @date 2025-03-31
 */
@Data
public class CodeGenerateDTO {

    /** 数据源ID */
    @NotNull(message = "数据源ID不能为空")
    private Long dataSourceId;

    /**数据库名称 */
    @NotBlank(message = "数据库名称不能为空")
    private String dbName;

    /**表名 */
    @NotBlank(message = "表名不能为空")
    private String tableName;

    /** 生成的代码的包名 */
    @NotBlank(message = "待生成的包名不能为空")
    private String packageName;
    
}
