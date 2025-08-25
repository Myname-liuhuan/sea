package com.example.sea.common.security.constants;

/**
 * 安全相关常量
 * @author liuhuan
 * @date 2025-08-25
 */
public interface SecurityConstants {

    /** 内部服务间调用 feign */
    String INTERNAL_FEIGN = "INTERNAL-FEIGN";

    /** token分类 accessToken */
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    /** token分类 refreshToken */
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
    /** 用户名称 */
    public static final String CLAIM_USERNAME = "username";
    /** 角色 */
    public static final String CLAIM_ROLES = "roles";
    /** 权限字符 */
    public static final String CLAIM_AUTHS = "authorities";
    /** tokenType */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
}