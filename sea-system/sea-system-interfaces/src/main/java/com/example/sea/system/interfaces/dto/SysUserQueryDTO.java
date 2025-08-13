package com.example.sea.system.interfaces.dto;

import lombok.Data;

/**
 * 用户数据查询参数传输对象
 * @author liuhuan
 * @date 2025-05-28
 */
@Data
public class SysUserQueryDTO {

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 已验证邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String profile;

}
