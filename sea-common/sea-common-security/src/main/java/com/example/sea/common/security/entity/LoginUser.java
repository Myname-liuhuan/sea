package com.example.sea.common.security.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

/**
 * 登录用户信息实体类
 */
@Data
public class LoginUser implements UserDetails {

    private Long id;

    private String username;

    private String password;
    
    /**
     * 角色编码列表
     */
    List<String> roles;

    /** 授权信息列表 */
    List<String> perms;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将字符串权限转换为GrantedAuthority对象
        return perms.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账户未过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 账户未锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证未过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账户启用
    }
    
}
