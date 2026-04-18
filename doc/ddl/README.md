# sea DDL 文档

## 概述

本目录包含 sea 项目的完整数据库设计 DDL 脚本，共分为 3 个数据库：

| 数据库名 | 用途 | 表数量 |
|---------|------|-------|
| `sea_system` | 系统管理模块 | 14 |
| `sea_business` | 业务模块 | 3 |
| `sea_codegen` | 代码生成模块 | 1 |

## 目录结构

```
sea/doc/ddl/
├── README.md              # 本文档
├── ALL.sql                # 汇总执行脚本
├── sea_system/            # sea_system 数据库
│   ├── 00_init.sql        # 数据库初始化
│   ├── 01_base.sql        # 用户、角色、部门表
│   ├── 02_auth.sql        # 菜单、权限关联表
│   ├── 03_audit.sql        # 登录日志、操作日志表
│   ├── 04_dict.sql        # 数据字典表
│   ├── 05_config.sql      # 系统配置、公告表
│   └── 06_file.sql         # 文件管理表
├── sea_business/          # sea_business 数据库
│   ├── 00_init.sql        # 数据库初始化
│   └── 01_media.sql        # 歌手、专辑、音乐表
└── sea_codegen/           # sea_codegen 数据库
    ├── 00_init.sql        # 数据库初始化
    └── 01_codegen.sql     # 代码生成数据源表
```

## 执行顺序

### MySQL 命令行执行

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 按顺序执行脚本
source sea/doc/ddl/sea_system/00_init.sql;
source sea/doc/ddl/sea_system/01_base.sql;
source sea/doc/ddl/sea_system/02_auth.sql;
source sea/doc/ddl/sea_system/03_audit.sql;
source sea/doc/ddl/sea_system/04_dict.sql;
source sea/doc/ddl/sea_system/05_config.sql;
source sea/doc/ddl/sea_system/06_file.sql;

source sea/doc/ddl/sea_business/00_init.sql;
source sea/doc/ddl/sea_business/01_media.sql;

source sea/doc/ddl/sea_codegen/00_init.sql;
source sea/doc/ddl/sea_codegen/01_codegen.sql;
```

### 或使用 MySQL 客户端工具

按目录顺序依次打开并执行 SQL 文件。

## 验证

执行完成后，使用以下命令验证表是否创建成功：

```sql
-- 查看 sea_system 数据库的表
SHOW TABLES FROM sea_system;

-- 查看 sea_business 数据库的表
SHOW TABLES FROM sea_business;

-- 查看 sea_codegen 数据库的表
SHOW TABLES FROM sea_codegen;

-- 验证表结构示例
SHOW CREATE TABLE sea_system.sys_user;
SHOW CREATE TABLE sea_business.d_singer;
```

## 表清单

### sea_system 数据库 (14 表)

| 序号 | 表名 | 说明 |
|-----|------|------|
| 1 | sys_user | 用户表 |
| 2 | sys_role | 角色表 |
| 3 | sys_dept | 部门表 |
| 4 | sys_menu | 菜单权限表 |
| 5 | sys_user_role | 用户角色关联表 |
| 6 | sys_role_menu | 角色菜单关联表 |
| 7 | sys_login_log | 登录日志表 |
| 8 | sys_operation_log | 操作日志表 |
| 9 | sys_dict | 数据字典表 |
| 10 | sys_dict_item | 数据字典项表 |
| 11 | sys_config | 系统配置表 |
| 12 | sys_notice | 系统公告表 |
| 13 | sys_notice_read | 公告阅读记录表 |
| 14 | sys_file | 文件管理表 |

### sea_business 数据库 (3 表)

| 序号 | 表名 | 说明 |
|-----|------|------|
| 1 | d_singer | 歌手表 |
| 2 | d_album | 专辑表 |
| 3 | d_music | 音乐表 |

### sea_codegen 数据库 (1 表)

| 序号 | 表名 | 说明 |
|-----|------|------|
| 1 | codegen_data_source | 代码生成数据源表 |

## 关联关系说明

> 注意：本项目不采用数据库外键约束，而是在 Java 应用层通过 MyBatis-Plus 维护关联关系。

### sea_system 关联关系

- `sys_user.dept_id` → `sys_dept.id`（用户在逻辑上归属部门）
- `sys_user_role.user_id` → `sys_user.id`
- `sys_user_role.role_id` → `sys_role.id`
- `sys_role_menu.role_id` → `sys_role.id`
- `sys_role_menu.menu_id` → `sys_menu.id`
- `sys_dict_item.dict_id` → `sys_dict.id`
- `sys_notice_read.notice_id` → `sys_notice.id`
- `sys_notice_read.user_id` → `sys_user.id`

### sea_business 关联关系

- `d_album.singer_id` → `d_singer.id`
- `d_music.singer_id` → `d_singer.id`
- `d_music.album_id` → `d_album.id`

## 维护说明

- 所有表都使用 InnoDB 引擎，支持事务
- 字符集统一使用 utf8mb4，支持 emoji 存储
- 每个表都有 `del_flag` 字段支持软删除
- 每个表都有 `create_time` 和 `update_time` 字段
- 大部分表支持逻辑删除 (del_flag = 0 表示未删除)
