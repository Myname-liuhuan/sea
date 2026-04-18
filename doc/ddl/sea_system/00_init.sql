-- ================================================================
-- sea_system 数据库初始化脚本
-- 用途：系统管理模块（用户、角色、权限、日志、字典等）
-- ================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS sea_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sea_system;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
