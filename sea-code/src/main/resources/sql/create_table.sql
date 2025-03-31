CREATE TABLE `codegen_data_source` (
    `id` BIGINT PRIMARY KEY COMMENT '主键 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
    `db_type` VARCHAR(50) NOT NULL COMMENT '数据库类型（MySQL, PostgreSQL, Oracle 等）',
    `host` VARCHAR(200) NOT NULL COMMENT '数据库主机地址',
    `port` INT NOT NULL COMMENT '数据库端口',
    `username` VARCHAR(100) NOT NULL COMMENT '数据库用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '数据库密码（建议加密存储）',
    `schema_name` VARCHAR(100) NOT NULL COMMENT '默认数据库/模式',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成 - 数据源信息表';
