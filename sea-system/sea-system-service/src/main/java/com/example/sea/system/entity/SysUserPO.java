package com.example.sea.system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;

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
public class SysUserPO extends BaseEntity{

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

    /** 部门 ID（与 sys_dept.id 关联） */
    private Long deptId;

    /** 直属上级 user_id */
    private Long leaderId;

    /** 能级：1-初级 5-高级 8-总监 10-CXO */
    private Integer level;

    /** 首次登录需改密：0 否 1 是 */
    private Integer requirePasswordChange;

}
