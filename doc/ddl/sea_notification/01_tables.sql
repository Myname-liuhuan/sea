-- ================================================================
-- sea_notification 业务表结构
-- 1. notify_template        通知模板
-- 2. notify_log             投递日志（含重试记录）
-- 3. in_app_message         站内信（铃铛未读源）
-- ================================================================

USE sea_notification;

-- ---------------------------------------------------------------
-- 1. notify_template
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS notify_template;
CREATE TABLE notify_template (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    template_code   VARCHAR(64)     NOT NULL                COMMENT '模板编码 e.g. PWD_RESET_OK',
    version         INT             NOT NULL DEFAULT 1      COMMENT '版本号',
    channel         VARCHAR(16)     NOT NULL                COMMENT 'IN_APP/EMAIL/SMS',
    subject         VARCHAR(255)                            COMMENT '邮件主题 / 短信签名 / 站内信标题',
    content         TEXT            NOT NULL                COMMENT '模板内容（占位 ${name}）',
    profile         VARCHAR(32)     NOT NULL DEFAULT 'default' COMMENT 'profile',
    enabled         TINYINT         NOT NULL DEFAULT 1,
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_ver_profile (template_code, version, profile),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板';

-- ---------------------------------------------------------------
-- 2. notify_log
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS notify_log;
CREATE TABLE notify_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    biz_key         VARCHAR(64)     NOT NULL                COMMENT '业务键（taskNo / 通用业务）',
    channel         VARCHAR(16)     NOT NULL                COMMENT 'IN_APP/EMAIL/SMS',
    receiver        VARCHAR(128)    NOT NULL                COMMENT '手机号 / 邮箱',
    user_id         BIGINT                                     COMMENT '目标用户 ID',
    template_code   VARCHAR(64)     NOT NULL,
    payload_cipher  TEXT                                        COMMENT '负载密文',
    status          VARCHAR(16)     NOT NULL DEFAULT 'PENDING'  COMMENT 'PENDING/SUCCESS/FAILED',
    error           VARCHAR(1000)                               COMMENT '错误堆栈摘要',
    attempts        INT             NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_biz (biz_key),
    KEY idx_status (status),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投递日志';

-- ---------------------------------------------------------------
-- 3. in_app_message
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS in_app_message;
CREATE TABLE in_app_message (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL                COMMENT '收信人',
    title           VARCHAR(255)    NOT NULL,
    content         TEXT            NOT NULL,
    link            VARCHAR(500)                                COMMENT '跳转链接',
    biz_key         VARCHAR(64)                                 COMMENT '业务键',
    read_flag       TINYINT         NOT NULL DEFAULT 0          COMMENT '0未读 1已读',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_unread (user_id, read_flag),
    KEY idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信';
