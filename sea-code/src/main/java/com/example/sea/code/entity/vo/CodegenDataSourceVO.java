package com.example.sea.code.entity.vo;

import java.time.LocalDateTime;

import com.example.sea.code.common.base.BaseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据源信息表VO
 * @author liuhuan
 * @date 2025-04-11
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CodegenDataSourceVO extends BaseVO{
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
