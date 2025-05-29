package com.example.sea.system.interfaces.dto;

import com.example.sea.common.entity.dto.BaseDTO;
import com.example.sea.common.validation.GroupInsert;
import com.example.sea.common.validation.GroupSave;

import jakarta.validation.constraints.NotBlank;
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

    /*** 主键id */
    @NotNull(message = "id不能为空", groups = {GroupInsert.class})
    private Long id;

    /**
     * 登录用户名
     */
    @NotBlank(message = "用户名不能为空",  groups = {GroupSave.class})
    private String username;

    /**
     * 已验证邮箱
     */
    @NotBlank(message = "邮箱不能为空", groups = {GroupSave.class})
    private String email;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * BCrypt加密
     */
    @NotBlank(message = "密码不能为空", groups = {GroupSave.class})
    private String password;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String profile;


}
