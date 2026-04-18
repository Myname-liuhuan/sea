-- ================================================================
-- sea_system 系统配置表
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 11. sys_config - 系统配置表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '配置ID',
    config_name     VARCHAR(100)    NOT NULL                    COMMENT '配置名称',
    config_key      VARCHAR(100)    NOT NULL                    COMMENT '配置键名',
    config_value    VARCHAR(500)    NOT NULL                    COMMENT '配置值',
    config_type     VARCHAR(20)     NOT NULL    DEFAULT 'string' COMMENT '配置类型(string,number,boolean,json)',
    description     VARCHAR(500)                              COMMENT '描述',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ---------------------------------------------------------------
-- 12. sys_notice - 系统公告表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '公告ID',
    notice_title    VARCHAR(200)    NOT NULL                    COMMENT '公告标题',
    notice_type     TINYINT         NOT NULL                    COMMENT '公告类型(1通知,2公告)',
    notice_content  TEXT                                      COMMENT '公告内容',
    target_type     TINYINT         NOT NULL    DEFAULT 0       COMMENT '推送范围(0全体,1指定用户,2指定角色)',
    target_ids      VARCHAR(500)                              COMMENT '目标ID列表',
    notice_status   TINYINT         NOT NULL    DEFAULT 0       COMMENT '发布状态(0草稿,1已发布,2已撤回)',
    publish_time    DATETIME                                  COMMENT '发布时间',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_notice_type (notice_type),
    KEY idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- ---------------------------------------------------------------
-- 13. sys_notice_read - 公告阅读记录表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_notice_read;
CREATE TABLE sys_notice_read (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    notice_id       BIGINT          NOT NULL                    COMMENT '公告ID',
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    read_time       DATETIME       NOT NULL                    COMMENT '阅读时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_notice_user (notice_id, user_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告阅读记录表';
