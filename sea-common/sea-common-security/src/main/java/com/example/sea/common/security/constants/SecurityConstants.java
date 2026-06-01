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
    String TOKEN_TYPE_ACCESS = "ACCESS";
    /** token分类 refreshToken */
    String TOKEN_TYPE_REFRESH = "REFRESH";
    /** 用户名称 */
    String CLAIM_USERNAME = "username";
    /** 角色 */
    String CLAIM_ROLES = "roles";
    /** 权限字符 */
    String CLAIM_AUTHS = "authorities";
    /** tokenType */
    String CLAIM_TOKEN_TYPE = "tokenType";
    /** 当前token的版本 */
    String CLAIM_VERSION = "version";
    
    /**token头*/
    String BEARER = "Bearer ";

}