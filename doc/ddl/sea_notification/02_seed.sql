-- ================================================================
-- sea_notification 种子：通知模板
-- 保持与同名 feature/feat-password-reset-workflow/2026_07_05_seed_notify_template.sql 一致
-- ================================================================

USE sea_notification;

-- 密码重置成功：站内信
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_OK', 1, 'IN_APP', '密码已重置',
 '您的密码已重置。临时密码：${pwd}。请尽快登录并修改。', 'default', 1, NOW(), 0);

-- 密码重置成功：邮件
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_OK', 1, 'EMAIL', '[${appName}] 密码已重置',
 '您好，您的账户密码已被管理员（审批人：${approver}）重置。\n临时密码：${pwd}\n有效期：30 分钟，请尽快登录并修改。',
 'default', 1, NOW(), 0);

-- 密码重置成功：短信
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_OK', 1, 'SMS', '海纳',
 '【${appName}】您的密码已被重置，临时密码：${pwd}，请尽快登录并修改。',
 'default', 1, NOW(), 0);

-- 工单被拒绝
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_REJECTED', 1, 'IN_APP', '密码重置申请被拒',
 '您的密码重置申请（工单 ${taskNo}）已被审批人拒绝。\n审批人：${approver}\n意见：${comment}\n如有疑问请联系 IT。',
 'default', 1, NOW(), 0);

-- 工单待审批（推给审批人）
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_PENDING', 1, 'IN_APP', '有待审批工单',
 '工单 ${taskNo}：${applicant} 申请重置 ${targetUser} 的密码，请尽快处理。',
 'default', 1, NOW(), 0);

-- 工单被通过（推给申请人）
INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_APPROVED', 1, 'IN_APP', '工单审批通过',
 '工单 ${taskNo} 已审批通过，密码正在重置中……',
 'default', 1, NOW(), 0);
