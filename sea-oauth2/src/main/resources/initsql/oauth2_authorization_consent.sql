CREATE TABLE oauth2_authorization_consent (
    registered_client_id VARCHAR(100) NOT NULL COMMENT '关联的已注册客户端 ID',
    principal_name       VARCHAR(200) NOT NULL COMMENT '授权主体名称，通常是用户名',
    authorities          VARCHAR(1000) NOT NULL COMMENT '已授予的权限列表（用空格或逗号分隔）',
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='OAuth2 授权同意表，记录用户对客户端的授权权限';
