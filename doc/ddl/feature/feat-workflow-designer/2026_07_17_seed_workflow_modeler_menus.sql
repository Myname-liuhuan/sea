-- ================================================================
-- 流程设计器（Web 端 BPMN Modeler）：sys_menu 种子数据
-- 父菜单 = 工作流（id=6）
-- 新增两个 MENU：
--   605 流程模型    (/workflow/model)          perms=workflow:model:read
--   606 流程设计器  (/workflow/designer)       perms=workflow:model:read
-- 新增四个 BUTTON（挂在 605 下）：
--   607 模型新建    perms=workflow:model:write
--   608 保存 BPMN   perms=workflow:model:write
--   609 部署流程    perms=workflow:model:deploy
--   610 删除模型    perms=workflow:model:delete
-- 用 INSERT IGNORE 保证幂等（重跑也不会报主键冲突）
-- ================================================================

USE sea_system;

INSERT IGNORE INTO sys_menu
  (id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status, create_time, del_flag) VALUES
  (605, 6,   '流程模型',   2, 4, '/workflow/model',     'workflow/model/index',    'workflow:model:read',   'Workflow', 1, 1, NOW(), 0),
  (606, 6,   '流程设计器', 2, 5, '/workflow/designer',  'workflow/designer/index', 'workflow:model:read',   'Edit',     1, 1, NOW(), 0),
  (607, 605, '新建模型',   3, 1, NULL, NULL, 'workflow:model:write',  '#', 1, 1, NOW(), 0),
  (608, 605, '保存 BPMN',  3, 2, NULL, NULL, 'workflow:model:write',  '#', 1, 1, NOW(), 0),
  (609, 605, '部署流程',   3, 3, NULL, NULL, 'workflow:model:deploy', '#', 1, 1, NOW(), 0),
  (610, 605, '删除模型',   3, 4, NULL, NULL, 'workflow:model:delete', '#', 1, 1, NOW(), 0);

-- 角色菜单关联
-- 管理员 (role_id=1) 拿全部 6 项（覆盖 read/write/deploy/delete）
INSERT IGNORE INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
  (400, 1, 605, NOW()),
  (401, 1, 606, NOW()),
  (402, 1, 607, NOW()),
  (403, 1, 608, NOW()),
  (404, 1, 609, NOW()),
  (405, 1, 610, NOW());

-- HR 专员 (role_id=3) 只读 + 设计（不放 deploy / delete，避免误操作线上流程）
INSERT IGNORE INTO sys_role_menu (id, role_id, menu_id, create_time) VALUES
  (410, 3, 605, NOW()),
  (411, 3, 606, NOW()),
  (412, 3, 607, NOW()),
  (413, 3, 608, NOW());