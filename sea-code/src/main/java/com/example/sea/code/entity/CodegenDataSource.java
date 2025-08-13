package com.example.sea.code.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 代码生成 - 数据源信息表实体类
 * @author liuhuan
 * @date 2025-03-31
 */
@Data
@Accessors(chain = true)
@TableName("codegen_data_source")
public class CodegenDataSource {


    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据库类型字符串（MySQL, PostgreSQL, Oracle 等）
     */
    private String dbType;

    /**
     * 数据库主机地址
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码（建议加密存储）
     */
    private String password;

    /**
     * 默认数据库/模式
     */
    private String schemaName;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
