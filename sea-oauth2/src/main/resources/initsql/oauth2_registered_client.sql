CREATE TABLE oauth2_registered_client (
    id VARCHAR(100) NOT NULL COMMENT '主键，唯一 ID',
    client_id VARCHAR(100) NOT NULL COMMENT '客户端 ID，必须唯一',
    client_id_issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '客户端 ID 签发时间',
    client_secret VARCHAR(200) DEFAULT NULL COMMENT '客户端密钥，可空',
    client_secret_expires_at TIMESTAMP DEFAULT NULL COMMENT '客户端密钥到期时间',
    client_name VARCHAR(200) NOT NULL COMMENT '客户端名称',
    client_authentication_methods VARCHAR(1000) NOT NULL COMMENT '客户端认证方式 JSON 列表',
    authorization_grant_types VARCHAR(1000) NOT NULL COMMENT '授权类型 JSON 列表',
    redirect_uris VARCHAR(1000) DEFAULT NULL COMMENT '回调 URI 列表',
    post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL COMMENT '登出重定向 URI 列表',
    scopes VARCHAR(1000) NOT NULL COMMENT '作用域 JSON 列表',
    client_settings VARCHAR(2000) NOT NULL COMMENT '客户端设置 JSON',
    token_settings VARCHAR(2000) NOT NULL COMMENT '令牌设置 JSON',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2 注册客户端表';