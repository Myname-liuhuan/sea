-- ================================================================
-- 重置密码工单化：通知模板种子
-- 同步目标：sea_notification/02_seed.sql
-- ================================================================

USE sea_notification;

INSERT INTO notify_template (template_code, version, channel, subject, content, profile, enabled, create_time, del_flag) VALUES
('PWD_RESET_OK', 1, 'IN_APP', '密码已重置',
 '您的密码已重置。临时密码：${pwd}。请尽快登录并修改。', 'default', 1, NOW(), 0),
('PWD_RESET_OK', 1, 'EMAIL', '[${appName}] 密码已重置',
 '您好，您的账户密码已被管理员（审批人：${approver}）重置。\n临时密码：${pwd}\n有效期：30 分钟，请尽快登录并修改。',
 'default', 1, NOW(), 0),
('PWD_RESET_OK', 1, 'SMS', '海纳',
 '【${appName}】您的密码已被重置，临时密码：${pwd}，请尽快登录并修改。',
 'default', 1, NOW(), 0),
('PWD_RESET_REJECTED', 1, 'IN_APP', '密码重置申请被拒',
 '您的密码重置申请（工单 ${taskNo}）已被审批人拒绝。\n审批人：${approver}\n意见：${comment}\n如有疑问请联系 IT。',
 'default', 1, NOW(), 0),
('PWD_RESET_PENDING', 1, 'IN_APP', '有待审批工单',
 '工单 ${taskNo}：${applicant} 申请重置 ${targetUser} 的密码，请尽快处理。',
 'default', 1, NOW(), 0),
('PWD_RESET_APPROVED', 1, 'IN_APP', '工单审批通过',
 '工单 ${taskNo} 已审批通过，密码正在重置中……',
 'default', 1, NOW(), 0);
