package com.example.sea.system.interfaces.dto;

import java.time.LocalDateTime;

import com.example.sea.common.entity.dto.BaseDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户数据传输对象
 * @author liuhuan
 * @date 2025-05-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDTO extends BaseDTO {

    /**
     * 登录用户名
     */
    @NotNull(message = "用户名不能为空")
    private String username;

    /**
     * 已验证邮箱
     */
    private  String  email;

    /**
     * BCrypt加密
     */
    private  String  passwordHash;

    /**
     * 头像URL
     */
    private  String  avatarUrl;

    /**
     * 个人简介
     */
    private  String  profile;

    /**
     * 封禁状态
     */
    private  String  isBanned;

    /**
     * 封禁截止时间
     */
    private  LocalDateTime  bannedUntil;
}
