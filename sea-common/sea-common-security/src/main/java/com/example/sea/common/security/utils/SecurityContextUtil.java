package com.example.sea.common.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.sea.common.security.entity.LoginUser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * SecurityContext工具类 - 提供静态方法获取认证信息
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {
        // 工具类，防止实例化
    }

    /**
     * 获取当前认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return Optional.ofNullable(getLoginUser())
                .map(LoginUser::getId)
                .orElse(null);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        Authentication authentication = getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * 获取当前LoginUser对象
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户权限
     */
    public static List<String> getAuthorities() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getPerms() : Collections.emptyList();
    }

    /**
     * 检查用户是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 检查用户是否有指定权限
     */
    public static boolean hasAuthority(String authority) {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && loginUser.getPerms().contains(authority);
    }

    /**
     * 获取当前JWT token
     */
    public static String getToken() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null;
    }
}
