package com.example.sea.common.security.entity;

import java.util.List;

import lombok.Data;

/**
 * 登录用户信息实体类
 */
@Data
public class LoginUser {

    private Long id;

    private String username;

    private String password;
    
    List<String> roles;

    /** 授权信息 */
    List<String> authorities;
    
}
