package com.example.sea.common.security.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 登录用户信息实体类
 */
@Data
public class LoginUser implements UserDetails {

    private Long id;

    private String username;

    private String password;

    /** 记录该userId当前token的版本 用于踢人下线*/
    private Long version;

    /**
     * 角色编码列表
     */
    List<String> roles;

    /** 授权信息列表 */
    List<String> perms;

    /**
     * 是否需要强制改密（重置密码首次登录场景）。
     * 重置密码时 sea-workflow 置 1，前端登录后判断并强制跳改密页；改密成功后置 0。
     */
    private Boolean requirePasswordChange;


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // §15 #25：既要带 perms（hasAuthority 校验），也要带 roles（hasRole 校验）。
        // 之前只把 perms 转成 GrantedAuthority，roles 字段完全没用上 → @PreAuthorize("hasRole('ADMIN')")
        // 永远 false，admin-emergency-reset 等强依赖角色的接口全部 403。
        java.util.List<GrantedAuthority> all = new java.util.ArrayList<>();
        if (roles != null) {
            roles.stream()
                    .filter(r -> r != null && !r.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(all::add);
        }
        if (perms != null) {
            perms.stream()
                    .filter(perm -> perm != null && !perm.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(all::add);
        }
        return all;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true; // 账户未过期
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true; // 账户未锁定
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 凭证未过期
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true; // 账户启用
    }
    
}
