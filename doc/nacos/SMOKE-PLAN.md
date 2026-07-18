# 端到端冒烟步骤（M5 演练手册）

> 本文档是 M5 之后的运维 / QA 演练手册，按顺序起服务、做端到端验证。CI 自动化冒烟可对齐本手册拆用例。

## 0. 前置依赖

| 依赖 | 期望版本 / 配置 |
|------|----------------|
| MySQL | 8.x；3 个库：sea_system / sea_workflow / sea_notification |
| Nacos | 2.x；listening on 8848 |
| Redis | 7.x；listening on 6379 |
| JVM | 17 |
| 邮件 / 短信 | dev 用 GreenMail 本地起 / 短信落 notify_log |

## 1. 数据库初始化（首次或迁移）

```bash
# 1) sea_system
mysql sea_system < sea/doc/ddl/sea_system/00_init.sql
mysql sea_system < sea/doc/ddl/sea_system/01_base.sql
mysql sea_system < sea/doc/ddl/sea_system/02_auth.sql
mysql sea_system < sea/doc/ddl/sea_system/03_audit.sql
mysql sea_system < sea/doc/ddl/sea_system/04_dict.sql
mysql sea_system < sea/doc/ddl/sea_system/05_config.sql
mysql sea_system < sea/doc/ddl/sea_system/06_file.sql
mysql sea_system < sea/doc/ddl/sea_system/07_permission_data.sql

# 2) sea_workflow
mysql < sea/doc/ddl/sea_workflow/00_init.sql
mysql sea_workflow < sea/doc/ddl/sea_workflow/01_business_tables.sql
mysql sea_workflow < sea/doc/ddl/sea_workflow/02_admin_audit.sql
# ACT_* 由 flowable-spring-boot-starter 自动建表

# 3) sea_notification
mysql < sea/doc/ddl/sea_notification/00_init.sql
mysql sea_notification < sea/doc/ddl/sea_notification/01_tables.sql
mysql sea_notification < sea/doc/ddl/sea_notification/02_seed.sql
```

或者各分支迁移：

```bash
mysql sea_system   < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_04_alter_*.sql
mysql sea_system   < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_04_seed_sys_menu_workflow.sql
mysql < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_04_create_sea_workflow_db.sql
mysql < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_04_create_sea_notification_db.sql
mysql sea_notification < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_05_seed_notify_template.sql
mysql sea_workflow < sea/doc/ddl/feature/feat-password-reset-workflow/2026_07_06_create_admin_audit.sql
```

## 2. 上传 Nacos 配置

按 `doc/nacos/sea-workflow.yaml` 与 `doc/nacos/sea-notification.yaml` 上传 Data ID。生产环境需替换 `${xxx}` 占位符。

## 3. 启动顺序

```bash
nohup java -jar sea-auth.jar          > log/auth.log     2>&1 &
sleep 2
nohup java -jar sea-system.jar        > log/system.log   2>&1 &
sleep 2
nohup java -jar sea-notification.jar  > log/notify.log   2>&1 &
sleep 2
nohup java -jar sea-workflow.jar      > log/workflow.log 2>&1 &
sleep 2
nohup java -jar sea-gateway.jar       > log/gateway.log  2>&1 &

cd sea-frontend && npm run dev   # http://localhost:3000
```

## 4. UI 验证流程

### 4.1 申请重置（普通用户都可）

1. 用 admin / admin123 登录（来自 07_permission_data.sql）
2. 进入"系统管理 > 用户管理"
3. 找到 target 用户，点"申请重置"（v-hasPermi='workflow:apply'）
4. 填"申请原因"+"紧急程度"，提交
5. 期待：弹"工单 XXX 已提交"，跳到"我的申请"

### 4.2 审批链路（部门主管）

1. 用 target 用户的直属上级账号登录
2. 进入"工作流 > 待我审批"
3. 应能看到刚提交的工单，点"通过"
4. 写意见（可选），确认
5. 期待：当前节点推进。若 level ≥ 8，自动进入 HR 节点

### 4.3 HR 复审（任意 role_code='HR' 用户）

1. 用 HR 角色账号登录
2. 进入"工作流 > 待我审批"，应看见 HR 节点工单
3. 通过 → 进入执行节点

### 4.4 执行 + 推送

1. 执行节点（PasswordResetDelegate）自动跑：
   - 生成临时密码
   - 调 sea-system 写 password_hash
   - 调 sea-notification 推送三通道
2. 目标用户登录会看到 require_password_change=1，登录后被强制改密
3. 用户收到：
   - 站内信（in_app_message）
   - 邮件（SMTP）
   - SMS（阿里云；dev 关闭）

### 4.5 铃铛未读

1. 浏览器停在任意页，打开 DevTools Network
2. 应能看到 ws://localhost:8080/api/notification/ws/notify?token=&lt;accessToken&gt;（经网关）
3. 收到推送 → unread 自增 + bell-badge 出现

### 4.6 工单监控（ADMIN）

1. admin 登录，进入"工作流 > 工单监控"
2. 看所有工单，状态徽章颜色匹配（待审批灰、审批中蓝、已通过绿、已拒绝红、已完成深绿）

### 4.7 紧急通道

1. admin 调用 API：
   ```bash
   curl -X POST http://localhost:9080/api/workflow/admin-emergency-reset \
     -H "Authorization: Bearer $TOK" \
     -H "Content-Type: application/json" \
     -d '{"targetUserId":2,"reason":"locked out"}'
   ```
2. 期待：响应 success，admin_audit 多一行 [OK]
3. sea-log 或 sea-system 直接读取 admin_audit 确认

## 5. 失败恢复路径

| 失败模式 | 预期行为 | 处理 |
|---------|---------|------|
| sea-system 不可用 | apply 失败，前端提示"目标用户不存在"或类似 | 恢复 sea-system |
| Flowable 不可用 | apply 写 task 失败，事务回滚 | 重启 workflow 服务 |
| 短信 vendor 失败 | notify_log 写 FAILED，下次 cron 重试 | 上线 vendor 凭据 |
| 用户 unlock 后第一次登录 | require_password_change=1 → 后端拦截改密 | — |

## 6. 自动化冒烟要点（CI 友好）

1. 启动 GreenMail SMTP（端口 3025） + 内存 JWT，dev profile
2. 注入 admin / 普通用户 / HR 角色用户三个种子
3. 跑 admin 申请 → 主管账号审批 → HR 审批
4. 断言：
   - workflow_task.status = COMPLETED
   - sys_user.password_hash 已更新（与 bcrypt 比较）
   - in_app_message 有新行
   - notify_log 至少一行 SUCCESS
   - admin_audit（admin 通道）新增一行
