package com.example.sea.code.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据源表入参dto
 * @author liuhuan
 * @date 2025-03-31
 */
@Data
public class CodeGenDataSourceDTO {

   
    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空", groups = {GroupSave.class})
    private String name;

    /**
     * 数据库类型字符串（MySQL, PostgreSQL, Oracle 等）
     */
    @NotBlank(message = "数据库类型不能为空", groups = {GroupCheck.class, GroupSave.class})
    private String dbType;

    /**
     * 数据库主机地址
     */
    @NotBlank(message = "数据库主机地址不能为空", groups = {GroupCheck.class, GroupSave.class})
    private String host;

    /**
     * 数据库端口
     */
    @NotNull(message = "数据库端口不能为空", groups = {GroupCheck.class, GroupSave.class})
    private Integer port;

    /**
     * 数据库用户名
     */
    @NotBlank(message = "数据库用户名不能为空", groups = {GroupCheck.class, GroupSave.class})
    private String username;

    /**
     * 数据库密码
     */
    @NotBlank(message = "数据库密码不能为空", groups = {GroupCheck.class, GroupSave.class})
    private String password;
    
}
