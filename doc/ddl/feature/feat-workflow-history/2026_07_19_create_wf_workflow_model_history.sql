-- ================================================================
-- 流程设计器历史版本表：wf_workflow_model_history
-- 每次保存 BPMN 自动落一条历史（version 单调递增），可用于回滚与 diff。
-- 现行版本（当前 BPMN）仍存在 ACT_RE_MODEL.EDITOR_SOURCE_ 字节流中。
-- ================================================================

USE sea_workflow;

CREATE TABLE IF NOT EXISTS wf_workflow_model_history (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    model_id        VARCHAR(64) NOT NULL                COMMENT '对应 ACT_RE_MODEL.ID_',
    version         INT         NOT NULL                COMMENT '该模型的历史版本号（单调递增，从 1 开始）',
    bpmn_xml        LONGTEXT    NOT NULL                COMMENT '历史 BPMN XML 全文',
    svg             LONGTEXT    NULL                    COMMENT '历史 SVG 预览',
    change_comment  VARCHAR(500) NULL                   COMMENT '本次保存说明（可选）',
    creator_id      BIGINT      NULL                    COMMENT '保存人 user_id',
    creator_name    VARCHAR(64) NULL                    COMMENT '保存人姓名',
    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag        TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_model_id (model_id),
    KEY idx_model_version (model_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程模型历史版本';