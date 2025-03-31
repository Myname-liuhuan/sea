package com.example.sea.code.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 代码生成接口入参
 * @author liuhuan
 * @date 2025-03-31
 */
@Data
public class CodeGenerateDTO {

    /**表名 */
    @NotBlank
    private String tableName;

    /** 生成的代码的包名 */
    @NotBlank
    private String packageName;
    
}
