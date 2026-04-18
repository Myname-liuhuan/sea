-- ================================================================
-- sea_system 文件管理表
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 14. sys_file - 文件管理表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '文件ID',
    file_name       VARCHAR(255)    NOT NULL                    COMMENT '文件名',
    original_name   VARCHAR(255)    NOT NULL                    COMMENT '原始文件名',
    file_suffix     VARCHAR(50)                               COMMENT '文件后缀',
    file_size       BIGINT                                    COMMENT '文件大小(字节)',
    file_url        VARCHAR(500)    NOT NULL                    COMMENT '文件访问URL',
    storage_mode    VARCHAR(20)     NOT NULL    DEFAULT 'local' COMMENT '存储方式',
    file_type       VARCHAR(50)                               COMMENT '文件类型',
    bucket_name     VARCHAR(100)                              COMMENT '存储桶名称',
    create_user     BIGINT                                     COMMENT '上传人',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_create_user (create_user),
    KEY idx_file_type (file_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件管理表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
