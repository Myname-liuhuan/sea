/*
IMPORTANT:
    If using PostgreSQL, update ALL columns defined with 'blob' to 'text',
    as PostgreSQL does not support the 'blob' data type.
*/
CREATE TABLE oauth2_authorization (
    id VARCHAR(100) NOT NULL COMMENT '主键，唯一授权 ID',
    registered_client_id VARCHAR(100) NOT NULL COMMENT '关联的已注册客户端 ID',
    principal_name VARCHAR(200) NOT NULL COMMENT '授权给予的用户或主体名称',
    authorization_grant_type VARCHAR(100) NOT NULL COMMENT '使用的授权类型，如 authorization_code、refresh_token 等',
    authorized_scopes VARCHAR(1000) DEFAULT NULL COMMENT '已授权的作用域列表（用空格分隔）',
    attributes BLOB DEFAULT NULL COMMENT '存储授权相关的附加属性（序列化后的二进制）',
    state VARCHAR(500) DEFAULT NULL COMMENT '授权码流程中的 state 值',
    authorization_code_value BLOB DEFAULT NULL COMMENT '授权码内容（序列化后的二进制）',
    authorization_code_issued_at TIMESTAMP DEFAULT NULL COMMENT '授权码签发时间',
    authorization_code_expires_at TIMESTAMP DEFAULT NULL COMMENT '授权码过期时间',
    authorization_code_metadata BLOB DEFAULT NULL COMMENT '授权码的元数据（序列化后的二进制）',
    access_token_value BLOB DEFAULT NULL COMMENT '访问令牌内容（序列化后的二进制）',
    access_token_issued_at TIMESTAMP DEFAULT NULL COMMENT '访问令牌签发时间',
    access_token_expires_at TIMESTAMP DEFAULT NULL COMMENT '访问令牌过期时间',
    access_token_metadata BLOB DEFAULT NULL COMMENT '访问令牌的元数据（序列化后的二进制）',
    access_token_type VARCHAR(100) DEFAULT NULL COMMENT '访问令牌类型，如 Bearer',
    access_token_scopes VARCHAR(1000) DEFAULT NULL COMMENT '访问令牌允许的作用域列表',
    oidc_id_token_value BLOB DEFAULT NULL COMMENT 'OIDC ID 令牌内容（序列化后的二进制）',
    oidc_id_token_issued_at TIMESTAMP DEFAULT NULL COMMENT 'OIDC ID 令牌签发时间',
    oidc_id_token_expires_at TIMESTAMP DEFAULT NULL COMMENT 'OIDC ID 令牌过期时间',
    oidc_id_token_metadata BLOB DEFAULT NULL COMMENT 'OIDC ID 令牌的元数据（序列化后的二进制）',
    refresh_token_value BLOB DEFAULT NULL COMMENT '刷新令牌内容（序列化后的二进制）',
    refresh_token_issued_at TIMESTAMP DEFAULT NULL COMMENT '刷新令牌签发时间',
    refresh_token_expires_at TIMESTAMP DEFAULT NULL COMMENT '刷新令牌过期时间',
    refresh_token_metadata BLOB DEFAULT NULL COMMENT '刷新令牌的元数据（序列化后的二进制）',
    user_code_value BLOB DEFAULT NULL COMMENT '设备码流程中的 user_code（序列化后的二进制）',
    user_code_issued_at TIMESTAMP DEFAULT NULL COMMENT 'user_code 签发时间',
    user_code_expires_at TIMESTAMP DEFAULT NULL COMMENT 'user_code 过期时间',
    user_code_metadata BLOB DEFAULT NULL COMMENT 'user_code 的元数据（序列化后的二进制）',
    device_code_value BLOB DEFAULT NULL COMMENT '设备码流程中的 device_code（序列化后的二进制）',
    device_code_issued_at TIMESTAMP DEFAULT NULL COMMENT 'device_code 签发时间',
    device_code_expires_at TIMESTAMP DEFAULT NULL COMMENT 'device_code 过期时间',
    device_code_metadata BLOB DEFAULT NULL COMMENT 'device_code 的元数据（序列化后的二进制）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COMMENT='OAuth2 授权表，用于持久化授权码、令牌、OIDC ID Token、设备码等';
