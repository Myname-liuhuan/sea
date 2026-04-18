-- ================================================================
-- sea_system 数据字典表
-- ================================================================

USE sea_system;

-- ---------------------------------------------------------------
-- 9. sys_dict - 数据字典表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '字典ID',
    dict_name       VARCHAR(100)    NOT NULL                    COMMENT '字典名称',
    dict_code       VARCHAR(100)    NOT NULL                    COMMENT '字典编码',
    description     VARCHAR(500)                              COMMENT '描述',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_code (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';

-- ---------------------------------------------------------------
-- 10. sys_dict_item - 数据字典项表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS sys_dict_item;
CREATE TABLE sys_dict_item (
    id              BIGINT          NOT NULL    AUTO_INCREMENT  COMMENT '字典项ID',
    dict_id         BIGINT          NOT NULL                    COMMENT '字典ID',
    item_text       VARCHAR(100)    NOT NULL                    COMMENT '字典项文本',
    item_value      VARCHAR(100)    NOT NULL                    COMMENT '字典项值',
    description     VARCHAR(500)                              COMMENT '描述',
    sort            INT             NOT NULL    DEFAULT 0       COMMENT '排序',
    status          TINYINT         NOT NULL    DEFAULT 1       COMMENT '状态(0停用,1正常)',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT                                     COMMENT '创建人',
    update_by       BIGINT                                     COMMENT '更新人',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_dict_id (dict_id),
    KEY idx_item_value (item_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典项表';
