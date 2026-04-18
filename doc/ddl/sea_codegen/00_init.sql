-- ================================================================
-- sea_codegen 数据库初始化脚本
-- 用途：代码生成模块（数据源配置）
-- ================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS sea_codegen
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sea_codegen;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
