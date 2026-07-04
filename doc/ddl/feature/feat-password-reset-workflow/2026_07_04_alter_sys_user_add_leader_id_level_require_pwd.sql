-- ================================================================
-- 重置密码工单化（M1）：sys_user 字段扩展
-- 适用：sea_system 库 ALTER（增量；不破坏现有数据）
-- 同分支要把对应列同步刷到 sea_system/01_base.sql
-- ================================================================

USE sea_system;

-- 1. 加字段
ALTER TABLE sys_user
    ADD COLUMN leader_id               BIGINT    NULL COMMENT '直属上级 user_id' AFTER dept_id,
    ADD COLUMN level                   TINYINT   NULL COMMENT '能级:1-初级 5-高级 8-总监 10-CXO' AFTER leader_id,
    ADD COLUMN require_password_change TINYINT   NOT NULL DEFAULT 0 COMMENT '首次登录需改密 0否1是' AFTER password_hash,
    ADD KEY idx_leader_id (leader_id);

-- 2. 老用户数据回填
--    leader_id 允许为 NULL（路由找不到上级则跳到 HR 角色组兜底）
--    level 默认 5（高级），由 HR 对照表后续批量校正
UPDATE sys_user SET level = 5 WHERE level IS NULL;
