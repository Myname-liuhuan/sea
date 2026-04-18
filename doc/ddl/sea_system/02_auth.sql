-- ================================================================
-- sea_system 权限中间表：用户角色、角色菜单关联
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 4. sys_menu - 菜单权限表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '菜单ID',
    parent_id       BIGINT          NOT NULL    DEFAULT 0        COMMENT '父菜单ID',
    menu_name       VARCHAR(50)     NOT NULL                    COMMENT '菜单名称',
    menu_type       TINYINT         NOT NULL                    COMMENT '类型(1目录,2菜单,3按钮)',
    order_num       INT             NOT NULL    DEFAULT 0       COMMENT '显示顺序',
    path            VARCHAR(200)                              COMMENT '路由地址',
    component       VARCHAR(255)                              COMMENT '组件路径',
    perms           VARCHAR(100)                              COMMENT '权限标识',
    icon            VARCHAR(100)                              COMMENT '菜单图标',
    visible         TINYINT         NOT NULL    DEFAULT 1       COMMENT '是否显示(0隐藏,1显示)',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_perms (perms)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ---------------------------------------------------------------
-- 5. sys_user_role - 用户角色关联表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                    COMMENT '角色ID',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ---------------------------------------------------------------
-- 6. sys_role_menu - 角色菜单关联表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '主键ID',
    role_id         BIGINT          NOT NULL                    COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL                    COMMENT '菜单ID',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';
