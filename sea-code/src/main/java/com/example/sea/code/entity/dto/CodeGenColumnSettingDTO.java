package com.example.sea.code.entity.dto;

import lombok.Data;

/**
 * 代码生成列设置
 * @author liuhuan
 * @date 2025-05-08
 */
@Data
public class CodeGenColumnSettingDTO {

    /** 列名 */
    private String columnName;

    /** 列类型 */
    private String columnType;

    /** 实体类类型 */
    private String entityType;

    /** 是否需要转化为实体类字段 */
    private Boolean isEntityField;
    
}
