-- ================================================================
-- sea_system 基础表：用户、角色、部门
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 1. sys_user - 用户表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL                    COMMENT '用户名',
    email           VARCHAR(100)                             COMMENT '邮箱',
    mobile          VARCHAR(20)                               COMMENT '手机号',
    password_hash   VARCHAR(255)    NOT NULL                    COMMENT '密码(BCrypt)',
    avatar_url      VARCHAR(500)                              COMMENT '头像URL',
    profile         VARCHAR(500)                              COMMENT '个人简介',
    dept_id         BIGINT                                     COMMENT '部门ID',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    is_banned       TINYINT         NOT NULL    DEFAULT 0       COMMENT '封禁状态(0正常,1封禁)',
    banned_until    DATETIME                                  COMMENT '封禁截止时间',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_mobile (mobile),
    KEY idx_email (email),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------------------------------------------------------------
-- 2. sys_role - 角色表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '角色ID',
    role_name       VARCHAR(50)     NOT NULL                    COMMENT '角色名称',
    role_code       VARCHAR(50)     NOT NULL                    COMMENT '角色编码',
    role_desc       VARCHAR(255)                             COMMENT '角色描述',
    data_scope      TINYINT         NOT NULL    DEFAULT 1       COMMENT '数据范围(1全部,2本部门及以下,3本部门,4本人)',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ---------------------------------------------------------------
-- 3. sys_dept - 部门表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '部门ID',
    parent_id       BIGINT          NOT NULL    DEFAULT 0        COMMENT '父部门ID',
    name            VARCHAR(50)     NOT NULL                    COMMENT '部门名称',
    order_num       INT             NOT NULL    DEFAULT 0       COMMENT '显示顺序',
    leader          VARCHAR(50)                              COMMENT '负责人',
    mobile          VARCHAR(20)                               COMMENT '联系电话',
    email           VARCHAR(100)                              COMMENT '邮箱',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';
