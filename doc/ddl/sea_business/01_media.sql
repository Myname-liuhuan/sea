-- ================================================================
-- sea_business 业务表：歌手、专辑、音乐
-- ================================================================

USE sea_business;

-- ---------------------------------------------------------------
-- 1. d_singer - 歌手表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS d_singer;
CREATE TABLE d_singer (
    id              BIGINT          NOT NULL                    COMMENT '歌手ID(雪花算法)',
    name            VARCHAR(100)    NOT NULL                    COMMENT '歌手名称',
    sex             TINYINT                                   COMMENT '性别(1男,0女)',
    singer_type     INT                                       COMMENT '歌手类型(1独立歌手,2乐队)',
    birthday        DATE                                      COMMENT '出生日期',
    create_user     BIGINT                                     COMMENT '创建人',
    update_user     BIGINT                                     COMMENT '更新人',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌手表';

-- ---------------------------------------------------------------
-- 2. d_album - 专辑表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS d_album;
CREATE TABLE d_album (
    id              BIGINT          NOT NULL                    COMMENT '专辑ID(雪花算法)',
    singer_id       BIGINT          NOT NULL                    COMMENT '歌手ID',
    album_name      VARCHAR(200)    NOT NULL                    COMMENT '专辑名称',
    album_image     VARCHAR(500)                              COMMENT '专辑封面',
    publish_date    DATE                                      COMMENT '发行日期',
    description     TEXT                                      COMMENT '专辑描述',
    create_user     BIGINT                                     COMMENT '创建人',
    update_user     BIGINT                                     COMMENT '更新人',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_singer_id (singer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专辑表';

-- ---------------------------------------------------------------
-- 3. d_music - 音乐表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS d_music;
CREATE TABLE d_music (
    id              BIGINT          NOT NULL                    COMMENT '音乐ID(雪花算法)',
    singer_id       BIGINT          NOT NULL                    COMMENT '歌手ID',
    album_id        BIGINT                                     COMMENT '专辑ID',
    music_name      VARCHAR(200)    NOT NULL                    COMMENT '音乐名称',
    music_url       VARCHAR(500)    NOT NULL                    COMMENT '音乐文件URL',
    image_url       VARCHAR(500)                              COMMENT '音乐封面',
    mini_image_url  VARCHAR(500)                              COMMENT '加密缩略图',
    music_time_length INT                                    COMMENT '音乐时长(秒)',
    lyric_url       VARCHAR(500)                              COMMENT '歌词文件URL',
    create_user     BIGINT                                     COMMENT '创建人',
    update_user     BIGINT                                     COMMENT '更新人',
    create_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        TINYINT         NOT NULL    DEFAULT 0       COMMENT '删除标志(0未删,1已删)',
    PRIMARY KEY (id),
    KEY idx_singer_id (singer_id),
    KEY idx_album_id (album_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
