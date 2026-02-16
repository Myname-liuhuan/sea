package com.example.sea.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 代码生成 - 数据源信息表实体类
 * @author liuhuan
 * @date 2025-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("codegen_data_source")
public class CodegenDataSource extends BaseEntity {

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

}
