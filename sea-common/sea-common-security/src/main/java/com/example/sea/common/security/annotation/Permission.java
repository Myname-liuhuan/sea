package com.example.sea.common.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 权限注解
 * 使用方式: @Permission("sys:user:add")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("hasAuthority('$value')")
public @interface Permission {

    /**
     * 权限标识，如 sys:user:add
     */
    String value();
}