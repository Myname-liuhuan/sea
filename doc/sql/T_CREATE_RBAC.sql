-- 基于角色的权限控制表设计
-- 1. 用户表
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '主键id',
  `username` varchar(50) NOT NULL COMMENT '登录用户名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(255) DEFAULT NULL COMMENT '已验证邮箱',
  `password_hash` varchar(255) NOT NULL COMMENT '加密密码（BCrypt/Argon2等）',
  `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `profile` text COMMENT '个人简介',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0停用 1正常',
  `is_banned` tinyint(1) DEFAULT 0 COMMENT '封禁状态 0正常 1封禁',
  `banned_until` datetime DEFAULT NULL COMMENT '封禁截止时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0:有效 1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 部门表
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL COMMENT '主键id',
  `parent_id` bigint DEFAULT 0 COMMENT '父部门ID',
  `name` varchar(100) NOT NULL COMMENT '部门名称',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) DEFAULT 1 COMMENT '部门状态 0停用 1正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0:有效 1:删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 3. 角色表
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '主键id',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码(唯一)',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `role_sort` int DEFAULT 0 COMMENT '显示顺序',
  `data_scope` tinyint(1) DEFAULT 1 COMMENT '数据范围 1全部 2本部门及以下 3本部门 4本人',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0停用 1正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0:有效 1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 4. 菜单/权限表
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '主键id',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint(1) NOT NULL COMMENT '类型 1目录 2菜单 3按钮',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识 sys:user:add',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `visible` tinyint(1) DEFAULT 1 COMMENT '是否显示 0隐藏 1显示',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0停用 1正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0:有效 1:删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 5. 用户-角色关联表
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- 6. 角色-菜单关联表
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- 7. 角色-部门关联表（数据权限）
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`role_id`, `dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与部门关联表';
