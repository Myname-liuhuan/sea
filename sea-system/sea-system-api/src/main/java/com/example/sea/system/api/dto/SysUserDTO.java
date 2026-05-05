package com.example.sea.system.api.dto;

import com.example.sea.common.core.entity.dto.BaseDTO;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户数据传输对象
 * @author liuhuan
 * @date 2025-05-28
 */
@Schema(description = "用户信息DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserDTO extends BaseDTO {

    /*** 主键id */
    @Schema(description = "用户ID （修改时必填）", example = "1")
    @NotNull(message = "id不能为空", groups = {GroupUpdate.class})
    private Long id;

    /**
     * 登录用户名
     */
    @Schema(description = "用户名 （新增时必填，修改时可选）")
    @NotBlank(message = "用户名不能为空",  groups = {GroupInsert.class})
    private String username;

    /**
     * 已验证邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * BCrypt加密
     */
    @Schema(description = "密码 （新增时必填，修改时可选）")
    @NotBlank(message = "密码不能为空", groups = {GroupInsert.class})
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
