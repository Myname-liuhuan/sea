package com.example.sea.system.api.param;

import com.example.sea.common.core.entity.param.BaseParam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户查询参数
 * @author liuhuan
 * @date 2026-04-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询参数")
public class SysUserQueryParam extends BaseParam {

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "已验证邮箱")
    private String email;

    @Schema(description = "联系电话")
    private String mobile;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "个人简介")
    private String profile;

    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;
}