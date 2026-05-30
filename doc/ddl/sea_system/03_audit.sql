-- ================================================================
-- sea_system 审计日志表：登录日志、操作日志
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 7. sys_login_log - 登录日志表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id              BIGINT          NOT NULL                        COMMENT '日志ID',
    user_id         BIGINT                                     COMMENT '用户ID',
    username        VARCHAR(50)                                COMMENT '用户名',
    ip_address      VARCHAR(128)                               COMMENT 'IP地址',
    login_location  VARCHAR(255)                              COMMENT '登录地点',
    browser         VARCHAR(100)                               COMMENT '浏览器',
    os              VARCHAR(100)                               COMMENT '操作系统',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '登录状态(0失败,1成功)',
    msg             VARCHAR(255)                              COMMENT '提示消息',
    login_time      DATETIME       NOT NULL                    COMMENT '登录时间',
    logout_time     DATETIME                                   COMMENT '退出时间',
    fail_reason     VARCHAR(255)                              COMMENT '失败原因',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_login_time (login_time),
    KEY idx_ip_address (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ---------------------------------------------------------------
-- 8. sys_operation_log - 操作日志表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '日志ID',
    title           VARCHAR(255)                              COMMENT '操作标题',
    business_type   VARCHAR(20)                              COMMENT '业务类型',
    method          VARCHAR(200)                              COMMENT '请求方法',
    request_method  VARCHAR(10)                               COMMENT '请求方式',
    operator_type   TINYINT                                  COMMENT '操作类型(0其它,1后台用户,2手机端用户)',
    user_id         BIGINT                                     COMMENT '用户ID',
    username        VARCHAR(50)                               COMMENT '用户名',
    operation_url   VARCHAR(255)                              COMMENT '请求URL',
    operation_ip    VARCHAR(128)                              COMMENT '操作IP地址',
    operation_location VARCHAR(255)                           COMMENT '操作地点',
    operation_param VARCHAR(2000)                             COMMENT '请求参数',
    response_param  VARCHAR(2000)                             COMMENT '返回参数',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '操作状态(0异常,1正常)',
    error_msg       TEXT                                      COMMENT '错误消息',
    operation_time  DATETIME       NOT NULL                    COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';