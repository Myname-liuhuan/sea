-- ================================================================
-- 重置密码工单化：HR 角色 + 用户 + 权限关联
-- 同步目标：sea_system/07_permission_data.sql（增量并入）
-- ================================================================

USE sea_system;

-- 1. HR 角色（id=3，复用 sys_role 表）
INSERT INTO sys_role (id, role_name, role_code, role_desc, data_scope, status, create_time, del_flag) VALUES
(3, 'HR 专员', 'HR', '人力资源专员，负责 level ≥ 8 用户的密码重置审批', 1, 1, NOW(), 0);

-- 2. HR 用户（id=3）
INSERT INTO sys_user (id, username, email, mobile, password_hash, dept_id, level, status, create_time, del_flag) VALUES
(3, 'hr_specialist', 'hr@sea.com', '13800138003', '$2a$10$tDx7HJ5YExDZcKKfC59oTeeWfcbaqbGjatGWJHFUCaIfSHZi3/QQq', 1, 8, 1, NOW(), 0);

-- 3. 用户 ↔ 角色：hr_specialist 是 HR
INSERT INTO sys_user_role (id, user_id, role_id, create_time) VALUES
(3, 3, 3, NOW());

-- 4. HR 角色 → 菜单权限
--    HR 主要进"待我审批"页面（workflow:approve），可以浏览"我的申请"，
--    没有"工单监控"权限（除非 admin 角色叠加）
INSERT INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
(300, 3, 6, NOW()),     -- 工作流目录
(301, 3, 601, NOW()),    -- 我的申请
(302, 3, 602, NOW());    -- 待我审批
