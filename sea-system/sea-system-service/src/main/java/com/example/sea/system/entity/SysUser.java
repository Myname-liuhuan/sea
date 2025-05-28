package com.example.sea.system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 用户表实体类
 * @author liuhuan
 * @date 2025-05-28
 */
@Data
@Accessors(chain = true)
@TableName("sys_users")
public class SysUser {

    /**
     * 主键id
     */
    private  Long  id;

    /**
     * 登录用户名
     */
    private  String  username;

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

    /**
     * 创建时间
     */
    private  LocalDateTime  createTime;

    /**
     * 更新时间
     */
    private  LocalDateTime  updateTime;

    /**
     * 创建人
     */
    private  Long  createBy;

    /**
     * 修改人
     */
    private  Long  updateBy;

    /**
     * 删除标识 0:有效  1:删除
     */
    private  String  delFlag;
}
