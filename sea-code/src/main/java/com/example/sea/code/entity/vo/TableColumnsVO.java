package com.example.sea.code.entity.vo;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 代码生成 - 表字段信息表视图对象
 * @author liuhuan
 * @date 2025-04-29
 */
@Data
@Accessors(chain = true)
public class TableColumnsVO  {
    
    /** 列名称 */
    private String columnName;

    /** 列类型 */
    private String columnType;

    /** 列对应的实体类类型 */
    private String entityType;

    /** 列注释 */
    private String columnComment;
}
