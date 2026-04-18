-- ================================================================
-- sea_business 数据库初始化脚本
-- 用途：业务模块（歌手、专辑、音乐等）
-- ================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS sea_business
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sea_business;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
