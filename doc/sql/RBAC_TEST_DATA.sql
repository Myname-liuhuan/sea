-- RBAC测试数据
-- 包含用户、角色、菜单及关联关系的完整测试数据
-- 用于测试 /treeMenu 接口

-- 1. 用户数据
-- 密码都是 '123456'，使用BCrypt加密
INSERT INTO `sys_user` (`id`, `username`, `mobile`, `email`, `password_hash`, `avatar_url`, `profile`, `status`, `is_banned`, `banned_until`, `last_login_time`, `create_time`, `update_time`, `create_by`, `update_by`, `del_flag`) VALUES
(1, 'admin', '13800138000', 'admin@sea.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', NULL, '系统管理员', 1, 0, NULL, NULL, NOW(), NOW(), NULL, NULL, 0),
(2, 'user', '13900139000', 'user@sea.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaUKk7h.T0mUO', NULL, '普通用户', 1, 0, NULL, NULL, NOW(), NOW(), NULL, NULL, 0);

-- 2. 角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `role_sort`, `data_scope`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `del_flag`) VALUES
(1, '管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限', 1, 1, 1, NOW(), NOW(), NULL, NULL, 0),
(2, '普通用户', 'ROLE_USER', '普通用户，拥有基础权限', 2, 1, 1, NOW(), NOW(), NULL, NULL, 0);

-- 3. 菜单数据
-- 首页模块
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `order_num`, `path`, `component`, `perms`, `icon`, `visible`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `del_flag`) VALUES
-- 首页目录
(1, 0, '首页', 1, 1, '/dashboard', 'Layout', NULL, 'dashboard', 1, 1, NOW(), NOW(), NULL, NULL, 0),
-- 首页子菜单
(2, 1, '仪表板', 2, 1, 'dashboard', 'dashboard/index', 'dashboard:view', 'dashboard', 1, 1, NOW(), NOW(), NULL, NULL, 0),
(3, 1, '工作台', 2, 2, 'workspace', 'workspace/index', 'workspace:view', 'work', 1, 1, NOW(), NOW(), NULL, NULL, 0);

-- 系统管理模块
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `order_num`, `path`, `component`, `perms`, `icon`, `visible`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `del_flag`) VALUES
-- 系统管理目录
(10, 0, '系统管理', 1, 10, '/system', 'Layout', NULL, 'system', 1, 1, NOW(), NOW(), NULL, NULL, 0),
-- 用户管理菜单
(11, 10, '用户管理', 2, 1, 'user', 'system/user/index', NULL, 'user', 1, 1, NOW(), NOW(), NULL, NULL, 0),
-- 用户管理按钮权限
(12, 11, '用户查询', 3, 1, NULL, NULL, 'sys:user:query', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(13, 11, '用户新增', 3, 2, NULL, NULL, 'sys:user:add', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(14, 11, '用户编辑', 3, 3, NULL, NULL, 'sys:user:edit', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(15, 11, '用户删除', 3, 4, NULL, NULL, 'sys:user:remove', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
-- 角色管理菜单
(16, 10, '角色管理', 2, 2, 'role', 'system/role/index', NULL, 'peoples', 1, 1, NOW(), NOW(), NULL, NULL, 0),
-- 角色管理按钮权限
(17, 16, '角色查询', 3, 1, NULL, NULL, 'sys:role:query', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(18, 16, '角色新增', 3, 2, NULL, NULL, 'sys:role:add', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(19, 16, '角色编辑', 3, 3, NULL, NULL, 'sys:role:edit', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(20, 16, '角色删除', 3, 4, NULL, NULL, 'sys:role:remove', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
-- 菜单管理菜单
(21, 10, '菜单管理', 2, 3, 'menu', 'system/menu/index', NULL, 'tree-table', 1, 1, NOW(), NOW(), NULL, NULL, 0),
-- 菜单管理按钮权限
(22, 21, '菜单查询', 3, 1, NULL, NULL, 'sys:menu:query', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(23, 21, '菜单新增', 3, 2, NULL, NULL, 'sys:menu:add', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(24, 21, '菜单编辑', 3, 3, NULL, NULL, 'sys:menu:edit', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0),
(25, 21, '菜单删除', 3, 4, NULL, NULL, 'sys:menu:remove', NULL, 0, 1, NOW(), NOW(), NULL, NULL, 0);

-- 4. 用户-角色关联关系
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_time`) VALUES
(1, 1, NOW()), -- admin用户关联管理员角色
(2, 2, NOW()); -- user用户关联普通用户角色

-- 5. 角色-菜单关联关系
-- 管理员角色拥有所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`) VALUES
(1, 1, NOW()), (1, 2, NOW()), (1, 3, NOW()), -- 首页模块
(1, 10, NOW()), (1, 11, NOW()), (1, 12, NOW()), (1, 13, NOW()), (1, 14, NOW()), (1, 15, NOW()), -- 用户管理
(1, 16, NOW()), (1, 17, NOW()), (1, 18, NOW()), (1, 19, NOW()), (1, 20, NOW()), -- 角色管理
(1, 21, NOW()), (1, 22, NOW()), (1, 23, NOW()), (1, 24, NOW()), (1, 25, NOW()); -- 菜单管理

-- 普通用户角色只拥有基础查看权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`) VALUES
(2, 1, NOW()), (2, 2, NOW()), (2, 3, NOW()), -- 首页模块
(2, 10, NOW()), (2, 11, NOW()), (2, 12, NOW()), -- 用户管理（仅查询）
(2, 16, NOW()), (2, 17, NOW()), -- 角色管理（仅查询）
(2, 21, NOW()), (2, 22, NOW()); -- 菜单管理（仅查询）

-- 测试说明：
-- 1. 密码统一为 '123456'，使用BCrypt加密
-- 2. 管理员用户 admin@sea.com 拥有所有权限
-- 3. 普通用户 user@sea.com 只有基础查看权限
-- 4. 菜单树结构：
--    首页（目录）
--      ├── 仪表板（菜单）
--      └── 工作台（菜单）
--    系统管理（目录）
--      ├── 用户管理（菜单）
--      │   ├── 用户查询（按钮）
--      │   ├── 用户新增（按钮）
--      │   ├── 用户编辑（按钮）
--      │   └── 用户删除（按钮）
--      ├── 角色管理（菜单）
--      │   ├── 角色查询（按钮）
--      │   ├── 角色新增（按钮）
--      │   ├── 角色编辑（按钮）
--      │   └── 角色删除（按钮）
--      └── 菜单管理（菜单）
--          ├── 菜单查询（按钮）
--          ├── 菜单新增（按钮）
--          ├── 菜单编辑（按钮）
--          └── 菜单删除（按钮）
