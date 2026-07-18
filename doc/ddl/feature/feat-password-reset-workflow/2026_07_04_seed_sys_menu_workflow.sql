-- ================================================================
-- 重置密码工单化：sys_menu 种子数据
-- 1) 新增一级菜单"工作流"（id=6）
-- 2) 挂载"我的申请 / 待我审批 / 工单监控"三条目
-- 3) 在用户管理（id=2）下加入"申请重置"按钮（id=601,menu_type=3）
-- 同分支同步刷 sea_system/07_permission_data.sql
-- ================================================================

USE sea_system;

-- 1) 一级菜单：工作流
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(6, 0, '工作流', 1, 50, '/workflow', NULL, NULL, 'SetUp', 1, 1, NOW(), 0);

-- 2) 工作流的子菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(601, 6, '我的申请',   2, 1, '/workflow/my',       'workflow/MyApplications',   'workflow:my',      'User',         1, 1, NOW(), 0),
(602, 6, '待我审批',   2, 2, '/workflow/pending',  'workflow/PendingApprovals', 'workflow:approve', 'Aim',          1, 1, NOW(), 0),
(603, 6, '工单监控',   2, 3, '/workflow/monitor',  'workflow/AllTasksMonitor',  'workflow:monitor', 'Monitor',      1, 1, NOW(), 0);

-- 3) 用户管理下的"申请重置"按钮（menu_type=3）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
(604, 2, '申请重置', 3, 4, NULL, NULL, 'workflow:apply', '#', 1, 1, NOW(), 0);

-- 4) 角色菜单关联：管理员拿到工作流全部；普通用户只有"我的申请"+"申请重置"按钮
INSERT INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
-- 管理员：工作流目录 + 三个菜单 + 用户管理按钮
(200, 1, 6, NOW()),
(201, 1, 601, NOW()),
(202, 1, 602, NOW()),
(203, 1, 603, NOW()),
(204, 1, 604, NOW()),
-- 普通用户：工作流目录 + 我的申请 + 用户管理"申请重置"按钮
(210, 2, 6, NOW()),
(211, 2, 601, NOW()),
(212, 2, 604, NOW());
