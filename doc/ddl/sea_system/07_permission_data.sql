-- ================================================================
-- sea_system 测试数据：让系统能跑起来
-- 执行前需先执行 DDL 建表
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 清空所有表数据（按依赖顺序）
-- ---------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE sys_operation_log;
TRUNCATE TABLE sys_login_log;
TRUNCATE TABLE sys_notice_read;
TRUNCATE TABLE sys_notice;
TRUNCATE TABLE sys_dict_item;
TRUNCATE TABLE sys_dict;
TRUNCATE TABLE sys_config;
TRUNCATE TABLE sys_role_menu;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_menu;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_dept;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_file;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------
-- 1. 部门数据 (sys_dept)
-- ---------------------------------------------------------------
INSERT INTO sys_dept (id, parent_id, name, order_num, leader, mobile, email, status, create_time, del_flag) VALUES
(1, 0, '总公司', 0, 'admin', '13800138000', 'admin@sea.com', 1, NOW(), 0),
(2, 1, '技术部', 1, 'tech', '13800138001', 'tech@sea.com', 1, NOW(), 0),
(3, 1, '运营部', 2, 'ops', '13800138002', 'ops@sea.com', 1, NOW(), 0),
(4, 2, '研发组', 3, 'dev', '13800138003', 'dev@sea.com', 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 2. 用户数据 (sys_user)
-- ---------------------------------------------------------------
-- 密码是 admin123 (BCrypt 加密后的结果)
INSERT INTO sys_user (id, username, email, mobile, password_hash, dept_id, level, require_password_change, status, create_time, del_flag) VALUES
(1, 'admin', 'admin@sea.com', '13800138000', '$2a$10$tDx7HJ5YExDZcKKfC59oTeeWfcbaqbGjatGWJHFUCaIfSHZi3/QQq', 1, 10, 0, 1, NOW(), 0),
(2, 'test', 'test@sea.com', '13800138001', '$2a$10$tDx7HJ5YExDZcKKfC59oTeeWfcbaqbGjatGWJHFUCaIfSHZi3/QQq', 2, 5, 0, 1, NOW(), 0),
(3, 'hr_specialist', 'hr@sea.com', '13800138003', '$2a$10$tDx7HJ5YExDZcKKfC59oTeeWfcbaqbGjatGWJHFUCaIfSHZi3/QQq', 1, 8, 0, 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 3. 角色数据 (sys_role)
-- ---------------------------------------------------------------
INSERT INTO sys_role (id, role_name, role_code, role_desc, data_scope, status, create_time, del_flag) VALUES
(1, '管理员', 'ROLE_ADMIN', '超级管理员，拥有所有权限', 1, 1, NOW(), 0),
(2, '普通用户', 'ROLE_USER', '普通用户，只有查询权限', 4, 1, NOW(), 0),
(3, 'HR 专员', 'HR', '人力资源专员，负责 level >= 8 用户的密码重置审批', 1, 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 4. 菜单数据 (sys_menu) - 包含目录、菜单、按钮
-- ---------------------------------------------------------------
-- 目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(1, 0, '系统管理', 1, 1, '/system', NULL, NULL, 'Setting', 1, 1, NOW(), 0),
(2, 1, '用户管理', 2, 1, '/system/user', 'system/user/index', 'sys:user:list', 'User', 1, 1, NOW(), 0),
(3, 1, '角色管理', 2, 2, '/system/role', 'system/role/index', 'sys:role:list', 'Role', 1, 1, NOW(), 0),
(4, 1, '菜单管理', 2, 3, '/system/menu', 'system/menu/index', 'sys:menu:list', 'Menu', 1, 1, NOW(), 0),
(5, 1, '部门管理', 2, 4, '/system/dept', 'system/dept/index', 'sys:dept:list', 'Office', 1, 1, NOW(), 0),
-- 工作流（重置密码工单化新增）
(6, 0, '工作流', 1, 50, '/workflow', NULL, NULL, 'SetUp', 1, 1, NOW(), 0);

-- 工作流子菜单（重置密码工单化新增）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(601, 6, '我的申请',   2, 1, '/workflow/my',      'workflow/MyApplications',   'workflow:my',      'User',    1, 1, NOW(), 0),
(602, 6, '待我审批',   2, 2, '/workflow/pending', 'workflow/PendingApprovals', 'workflow:approve', 'Aim',     1, 1, NOW(), 0),
(603, 6, '工单监控',   2, 3, '/workflow/monitor', 'workflow/AllTasksMonitor',  'workflow:monitor', 'Monitor', 1, 1, NOW(), 0),
-- 流程模型 / 设计器（流程设计器新增）
(605, 6, '流程模型',   2, 4, '/workflow/model',     'workflow/model/index',    'workflow:model:read', 'Workflow', 1, 1, NOW(), 0),
(606, 6, '流程设计器', 2, 5, '/workflow/designer',  'workflow/designer/index', 'workflow:model:read', 'Edit',     1, 1, NOW(), 0);

-- 按钮权限 (menu_type=3)
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
-- 用户管理按钮
(101, 2, '用户新增', 3, 1, NULL, NULL, 'sys:user:add', '#', 1, 1, NOW(), 0),
(102, 2, '用户编辑', 3, 2, NULL, NULL, 'sys:user:edit', '#', 1, 1, NOW(), 0),
(103, 2, '用户删除', 3, 3, NULL, NULL, 'sys:user:delete', '#', 1, 1, NOW(), 0),
-- 角色管理按钮
(201, 3, '角色新增', 3, 1, NULL, NULL, 'sys:role:add', '#', 1, 1, NOW(), 0),
(202, 3, '角色编辑', 3, 2, NULL, NULL, 'sys:role:edit', '#', 1, 1, NOW(), 0),
(203, 3, '角色删除', 3, 3, NULL, NULL, 'sys:role:delete', '#', 1, 1, NOW(), 0),
-- 菜单管理按钮
(301, 4, '菜单新增', 3, 1, NULL, NULL, 'sys:menu:add', '#', 1, 1, NOW(), 0),
(302, 4, '菜单编辑', 3, 2, NULL, NULL, 'sys:menu:edit', '#', 1, 1, NOW(), 0),
(303, 4, '菜单删除', 3, 3, NULL, NULL, 'sys:menu:delete', '#', 1, 1, NOW(), 0),
-- 部门管理按钮
(401, 5, '部门新增', 3, 1, NULL, NULL, 'sys:dept:add', '#', 1, 1, NOW(), 0),
(402, 5, '部门编辑', 3, 2, NULL, NULL, 'sys:dept:edit', '#', 1, 1, NOW(), 0),
(403, 5, '部门删除', 3, 3, NULL, NULL, 'sys:dept:delete', '#', 1, 1, NOW(), 0),
-- 用户管理新按钮（重置密码工单化新增）
(604, 2, '申请重置', 3, 4, NULL, NULL, 'workflow:apply', '#', 1, 1, NOW(), 0);

-- 流程模型按钮（流程设计器新增）：挂在菜单 605 下
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(607, 605, '新建模型',  3, 1, NULL, NULL, 'workflow:model:write',  '#', 1, 1, NOW(), 0),
(608, 605, '保存 BPMN', 3, 2, NULL, NULL, 'workflow:model:write',  '#', 1, 1, NOW(), 0),
(609, 605, '部署流程',  3, 3, NULL, NULL, 'workflow:model:deploy', '#', 1, 1, NOW(), 0),
(610, 605, '删除模型',  3, 4, NULL, NULL, 'workflow:model:delete', '#', 1, 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 5. 用户角色关联 (sys_user_role) - 添加 id 字段
-- ---------------------------------------------------------------
INSERT INTO sys_user_role (id, user_id, role_id, create_time) VALUES
(1, 1, 1, NOW()),  -- admin -> 管理员
(2, 2, 2, NOW()),  -- test -> 普通用户
(3, 3, 3, NOW());  -- hr_specialist -> HR 专员

-- ---------------------------------------------------------------
-- 6. 角色菜单关联 (sys_role_menu) - 添加 id 字段
-- ---------------------------------------------------------------
-- 管理员拥有所有权限
INSERT INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
-- 目录和菜单
(1, 1, 1, NOW()), (2, 1, 2, NOW()), (3, 1, 3, NOW()), (4, 1, 4, NOW()), (5, 1, 5, NOW()),
-- 工作流目录与子菜单（重置密码工单化新增）
(6, 1, 6, NOW()), (200, 1, 601, NOW()), (201, 1, 602, NOW()), (202, 1, 603, NOW()),
-- 用户管理按钮
(7, 1, 101, NOW()), (8, 1, 102, NOW()), (9, 1, 103, NOW()),
-- 用户管理"申请重置"按钮（重置密码工单化新增）
(203, 1, 604, NOW()),
-- 角色管理按钮
(10, 1, 201, NOW()), (11, 1, 202, NOW()), (12, 1, 203, NOW()),
-- 菜单管理按钮
(13, 1, 301, NOW()), (14, 1, 302, NOW()), (15, 1, 303, NOW()),
-- 部门管理按钮
(16, 1, 401, NOW()), (17, 1, 402, NOW()), (18, 1, 403, NOW()),
-- 流程模型 / 设计器菜单 + 按钮（流程设计器新增）
(400, 1, 605, NOW()), (401, 1, 606, NOW()),
(402, 1, 607, NOW()), (403, 1, 608, NOW()),
(404, 1, 609, NOW()), (405, 1, 610, NOW());

-- 普通用户只有查询权限 (只关联目录和菜单页面 + 我的申请 + 申请重置按钮)
INSERT INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
(19, 2, 1, NOW()), (20, 2, 2, NOW()), (21, 2, 3, NOW()), (22, 2, 4, NOW()), (23, 2, 5, NOW()),
-- 工作流目录与我的申请（重置密码工单化新增）
(24, 2, 6, NOW()), (210, 2, 601, NOW()),
-- 申请重置按钮（重置密码工单化新增）
(211, 2, 604, NOW());

-- HR 专员：进入待我审批节点（重置密码工单化新增）
INSERT INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
(300, 3, 6, NOW()),     -- 工作流目录
(301, 3, 601, NOW()),    -- 我的申请（可看自己的）
(302, 3, 602, NOW()),    -- 待我审批（核心：HR 节点在这里领）
-- 流程模型 / 设计器（流程设计器新增）：HR 只读 + 设计，不放 deploy/delete
(410, 3, 605, NOW()),
(411, 3, 606, NOW()),
(412, 3, 607, NOW()),
(413, 3, 608, NOW());

-- ---------------------------------------------------------------
-- 7. 配置数据 (sys_config)
-- ---------------------------------------------------------------
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, description, status, create_time, del_flag) VALUES
(1, '系统名称', 'sys.system.name', 'SEA系统', 'string', '系统名称', 1, NOW(), 0),
(2, '系统版本', 'sys.system.version', '1.0.0', 'string', '系统版本', 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 8. 字典数据 (sys_dict, sys_dict_item)
-- ---------------------------------------------------------------
INSERT INTO sys_dict (id, dict_name, dict_code, status, create_time, del_flag) VALUES
(1, '用户状态', 'sys_user_status', 1, NOW(), 0),
(2, '菜单类型', 'sys_menu_type', 1, NOW(), 0);

INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort, status, create_time, del_flag) VALUES
-- 用户状态字典项
(1, 1, '正常', '1', '正常状态', 1, 1, NOW(), 0),
(2, 1, '停用', '0', '停用状态', 2, 1, NOW(), 0),
-- 菜单类型字典项
(3, 2, '目录', '1', '目录类型', 1, 1, NOW(), 0),
(4, 2, '菜单', '2', '菜单类型', 2, 1, NOW(), 0),
(5, 2, '按钮', '3', '按钮类型', 3, 1, NOW(), 0);

-- ---------------------------------------------------------------
-- 9. 日志数据 (sys_login_log)
-- ---------------------------------------------------------------
INSERT INTO sys_login_log (id, user_id, username, ip_address, login_location, browser, os, status, msg, login_time) VALUES
(1, 1, 'admin', '127.0.0.1', '本机', 'Chrome', 'Windows 10', 1, '登录成功', NOW()),
(2, 2, 'test', '127.0.0.1', '本机', 'Chrome', 'MacOS', 1, '登录成功', NOW());

-- ---------------------------------------------------------------
-- 10. 文件表 (sys_file)
-- ---------------------------------------------------------------
INSERT INTO sys_file (id, file_name, original_name, file_suffix, file_size, file_url, storage_mode, file_type, bucket_name, create_user, create_time, del_flag) VALUES
(1, '示例文件.txt', '示例文件.txt', 'txt', 1024, '/files/sample.txt', 'local', 'text/plain', 'default', 1, NOW(), 0);