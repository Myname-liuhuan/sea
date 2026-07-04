-- ================================================================
-- sea_workflow 库 + 业务三表（workflow_task / approval / definition）
-- 重置密码工单化（M1）
-- 注：Flowable ACT_* 引擎表由 flowable-spring-boot-starter
--     在启动时按 database-schema-update 自动维护，本脚本不固化
-- ================================================================

CREATE DATABASE IF NOT EXISTS sea_workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sea_workflow;

-- ---------------------------------------------------------------
-- 1. workflow_task - 工单主表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS workflow_task;
CREATE TABLE workflow_task (
    id                          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_no                     VARCHAR(32)     NOT NULL                COMMENT '工单编号',
    business_type               VARCHAR(32)     NOT NULL DEFAULT 'PASSWORD_RESET' COMMENT '业务类型',
    business_key                VARCHAR(64)                          COMMENT '业务关联键',
    applicant_id                BIGINT          NOT NULL                COMMENT '申请人',
    target_user_id              BIGINT          NOT NULL                COMMENT '目标用户',
    reason                      VARCHAR(500)    NOT NULL                COMMENT '申请原因',
    urgency                     TINYINT         NOT NULL DEFAULT 1      COMMENT '紧急程度 1普通 2紧急',
    status                      TINYINT         NOT NULL DEFAULT 0      COMMENT '状态 0待审批 1审批中 2已通过 3已拒绝 4已撤回 5已完成',
    flow_instance_id            VARCHAR(64)                          COMMENT 'Flowable 实例 ID',
    current_node                VARCHAR(64)                          COMMENT '当前等待节点',
    temporary_password_cipher   VARCHAR(255)                         COMMENT '临时密码 AES 密文（admin 可见）',
    version                     INT             NOT NULL DEFAULT 0      COMMENT '乐观锁',
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by                   BIGINT                                   COMMENT '创建人',
    update_by                   BIGINT                                   COMMENT '更新人',
    del_flag                    TINYINT         NOT NULL DEFAULT 0      COMMENT '删除标志 0未删 1已删',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_applicant (applicant_id),
    KEY idx_target (target_user_id),
    KEY idx_status (status),
    KEY idx_flow_instance (flow_instance_id),
    KEY idx_business (business_type, business_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单主表';

-- ---------------------------------------------------------------
-- 2. workflow_approval - 审批记录
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS workflow_approval;
CREATE TABLE workflow_approval (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_id         BIGINT          NOT NULL                    COMMENT '工单 ID',
    node_key        VARCHAR(64)     NOT NULL                    COMMENT '节点标识 dept_leader/hr 等',
    node_order      INT             NOT NULL DEFAULT 0          COMMENT '审批层级',
    approver_id     BIGINT          NOT NULL                    COMMENT '实际操作人',
    approved        TINYINT         NOT NULL                    COMMENT '1通过 0拒绝',
    comment         VARCHAR(500)                                COMMENT '意见',
    delegated_from  BIGINT                                     COMMENT '转交来源（核心三项之一）',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_approver (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录';

-- ---------------------------------------------------------------
-- 3. workflow_definition - 流程定义元数据
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS workflow_definition;
CREATE TABLE workflow_definition (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    business_type   VARCHAR(32)     NOT NULL                    COMMENT '业务类型',
    version         INT             NOT NULL DEFAULT 1          COMMENT '版本号',
    definition_json JSON            NOT NULL                    COMMENT '节点/条件/审批人解析器键',
    enabled         TINYINT         NOT NULL DEFAULT 1          COMMENT '0停用 1启用',
    is_current      TINYINT         NOT NULL DEFAULT 0          COMMENT '当前启用版本 0否 1是',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_version (business_type, version),
    KEY idx_current (business_type, is_current)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义';
