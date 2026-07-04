-- ================================================================
-- sea_workflow.02_admin_audit.sql
-- 管理员紧急操作审计（与同名 feature 文件一致）
-- ================================================================

USE sea_workflow;

DROP TABLE IF EXISTS admin_audit;
CREATE TABLE admin_audit (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    operator_id     BIGINT          NOT NULL                COMMENT '操作人 admin user_id',
    action          VARCHAR(64)     NOT NULL                COMMENT 'EMERGENCY_RESET 等',
    target_user_id  BIGINT          NOT NULL                COMMENT '目标用户 user_id',
    reason          VARCHAR(500)                            COMMENT '操作原因',
    request_ip      VARCHAR(64)                             COMMENT '操作人来源 IP',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_operator (operator_id),
    KEY idx_target (target_user_id),
    KEY idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员紧急操作审计';
