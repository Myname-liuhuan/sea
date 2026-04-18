-- ================================================================
-- sea_codegen 代码生成数据源表
-- ================================================================

USE sea_codegen;

-- ---------------------------------------------------------------
-- 1. codegen_data_source - 代码生成数据源表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS codegen_data_source;
CREATE TABLE codegen_data_source (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '数据源ID',
    name            VARCHAR(100)    NOT NULL                    COMMENT '数据源名称',
    db_type         VARCHAR(20)     NOT NULL                    COMMENT '数据库类型',
    host            VARCHAR(100)    NOT NULL                    COMMENT '数据库主机',
    port            INT             NOT NULL                    COMMENT '数据库端口',
    username        VARCHAR(50)     NOT NULL                    COMMENT '用户名',
    password        VARCHAR(255)    NOT NULL                    COMMENT '密码',
    schema_name     VARCHAR(50)                              COMMENT '数据库/模式名',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0禁用,1启用)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_db_type (db_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码生成数据源表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
