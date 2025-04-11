CREATE TABLE `codegen_data_source` (
  `id` bigint NOT NULL COMMENT '主键 ID',
  `name` varchar(100) NOT NULL COMMENT '数据源名称',
  `db_type` varchar(50) NOT NULL COMMENT '数据库类型（MySQL, PostgreSQL, Oracle 等）',
  `host` varchar(200) NOT NULL COMMENT '数据库主机地址',
  `port` int NOT NULL COMMENT '数据库端口',
  `username` varchar(100) NOT NULL COMMENT '数据库用户名',
  `password` varchar(255) NOT NULL COMMENT '数据库密码（建议加密存储）',
  `schema_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '默认数据库/模式',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成 - 数据源信息表';