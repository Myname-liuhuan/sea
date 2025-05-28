------------------------------
-- 1. 用户表（核心实体）
------------------------------
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '主键id',
  `username` varchar(50) NOT NULL COMMENT '登录用户名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '已验证邮箱',
  `password_hash` char(60) NOT NULL COMMENT 'BCrypt加密',
  `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `profile` text COMMENT '个人简介',
  `is_banned` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '封禁状态 0正常  1封禁',
  `banned_until` datetime DEFAULT NULL COMMENT '封禁截止时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) unsigned zerofill DEFAULT NULL COMMENT '删除标识 0:有效  1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_sys_users_username` (`username`),
  UNIQUE KEY `idx_sys_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';