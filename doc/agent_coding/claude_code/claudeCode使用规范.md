# Claude Code / AI 编程助手 使用规范

> 目标：提高 AI 生成代码的准确率、稳定性、可维护性  
> 适用于：Claude Code、ChatGPT、Cursor、Copilot 等 AI 编程工具

---

## 一、核心原则

AI 编程效果 = 上下文质量 × 任务清晰度 × 约束程度

请始终遵循：

1. 目标明确
2. 上下文充分
3. 规则清晰
4. 小步迭代
5. 可验证结果

---

## 二、标准任务描述模板（推荐）

示例：

```
你是一个资深 Java 架构师

目标：
实现用户注册接口

技术栈：
Spring Boot 3 + MyBatis Plus + PostgreSQL

输入：
username, password, email

输出：
userId

约束：
- username 唯一
- 密码 bcrypt 加密
- 使用事务
- 返回 Result<T>

要求输出：
- 设计思路
- 数据库表结构
- 代码
- 风险点
```

---

## 三、必须提供的上下文信息

### 1. 项目结构

示例：

controller/
service/
mapper/
entity/
config/

### 2. 已存在基础类

例如：

BaseEntity:

createTime

updateTime

createUser

### 3. 技术选型

例如：

- Spring Boot 3
- MyBatis Plus
- PostgreSQL
- Redis
- RabbitMQ

---

## 四、CLAUDE.md 规则文件（强烈推荐 ⭐）

在项目根目录创建 `CLAUDE.md`，示例内容：

```
项目编码规范

返回结构：所有接口必须返回 Result<T>

架构约束：
- Controller 不写业务逻辑
- Service 处理业务
- Mapper 只做数据库访问

数据库规范：
- 表名使用下划线
- 字段使用 snake_case
- 必须包含 create_time

时间处理：
- 禁止使用 new Date()
- 统一使用 LocalDateTime

日志规范：
- 必须使用 log.info / log.error
- 禁止 System.out.println
```

> AI 会自动遵守项目规范，生成代码风格统一。

---

## 五、推荐任务拆分流程

❌ 错误方式：

> 写一个完整订单系统

✅ 正确方式：

步骤：

1️⃣ 设计数据库表

请先设计订单表结构

2️⃣ 生成 Entity + Mapper

根据表结构生成代码

3️⃣ 实现 Service

实现业务逻辑

4️⃣ 实现 Controller

写接口层

5️⃣ 编写测试

生成单元测试

成功率显著提高。

---

## 六、修改代码的正确方式

❌ 不推荐：

不对，重新写

✅ 推荐：

存在问题：

SQL 缺少索引

事务位置错误

返回结构不符合 Result<T>

请只修改以上问题

AI 修复能力远高于重写。

---

## 七、让 AI 先设计再编码（重要）

推荐流程：

先给出 3 种实现方案
比较优缺点
选择最优方案
再写代码

可以显著提高质量。

---

## 八、测试驱动方式（TDD）

推荐：

先写测试用例
再实现代码

适用于：

- 核心业务
- 高并发模块
- 复杂逻辑

---

## 九、提供真实错误信息

效果最好：

报错：

org.postgresql.util.PSQLException:
column xxx does not exist

避免：

数据库有问题

错误日志越真实，AI 越准确。

---

## 十、角色指令（提高质量）

示例：

你是一个资深后端架构师
熟悉高并发系统
代码必须生产级

可明显提升代码严谨程度。

---

## 十一、Skills / 自动化命令（进阶）

推荐封装命令：

/build
/test
/deploy
/git-commit
/run-sql

优势：

- 防止 AI 执行危险命令
- 跨平台兼容
- 自动化流程

---

## 十二、最佳实践工作流（推荐 ⭐⭐⭐）

完整流程：

1. 描述需求
2. AI 设计方案
3. 确认方案
4. AI 编码
5. AI 生成测试
6. 本地运行验证
7. AI 修复问题
8. 提交代码

---

## 十三、常见错误用法

### ❌ 需求模糊

写一个系统

### ❌ 没有上下文

写代码

### ❌ 一次任务过大

做一个商城

### ❌ 频繁推翻

全部重写

---

## 十四、专家级 Prompt 模板 ⭐⭐⭐

示例：

你是资深 Java 架构师

目标：
实现订单创建接口

技术：
Spring Boot + MyBatis Plus + PostgreSQL + Redis

要求：

高并发安全

事务正确

可扩展

代码生产级

输出：

设计思路

数据库表

核心代码

单元测试

风险分析

---

## 十五、效率提升关键结论

真正拉开差距的不是 Prompt 技巧，而是：

✅ 项目规则文件（CLAUDE.md）  
✅ 自动化 Skills 工具  
✅ 小步迭代开发  

---


## 十六、终极原则

> AI 不是替你写代码  
> AI 是你的高级工程师助手  

你的输入质量 = 输出质量

# 完