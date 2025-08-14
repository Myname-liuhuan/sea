package com.example.sea.system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.core.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 用户表实体类
 * @author liuhuan
 * @date 2025-05-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends BaseEntity{

    /**
     * 登录用户名
     */
    private  String  username;

    /**
     * 已验证邮箱
     */
    private  String  email;

    /**
     * 联系电话
     */
    private String mobile;

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

    /** '状态 0停用 1正常 */
    private String  status;

    /**
     * 封禁状态 0正常 1封禁
     */
    private  String  isBanned;

    /**
     * 封禁截止时间
     */
    private  LocalDateTime  bannedUntil;

}
