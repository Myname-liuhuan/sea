package com.example.sea.system.interfaces.vo;

import java.time.LocalDateTime;

import com.example.sea.common.core.entity.vo.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户视图对象
 * @author liuhuan
 * @date 2025-05-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserVO extends BaseVO {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  LocalDateTime  bannedUntil;
}
