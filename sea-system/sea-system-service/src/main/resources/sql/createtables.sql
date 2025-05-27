------------------------------
-- 1. 用户表（核心实体）
------------------------------
CREATE TABLE sys_users (
    id BIGINT PRIMARY KEY COMMENT '主键id',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '已验证邮箱',
    password_hash CHAR(60) NOT NULL COMMENT 'BCrypt加密',
    avatar_url VARCHAR(512) COMMENT '头像URL',
    profile TEXT COMMENT '个人简介',
    is_banned BOOLEAN NOT NULL DEFAULT FALSE COMMENT '封禁状态',
    banned_until DATETIME COMMENT '封禁截止时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	create_by BIGINT COMMENT '创建人',
	update_by BIGINT COMMENT '修改人',
	del_flag TINYINT(1)	COMMENT '删除标识 0:有效  1:删除'
) ENGINE=InnoDB CHARACTER SET utf8mb4 COMMENT '用户表';