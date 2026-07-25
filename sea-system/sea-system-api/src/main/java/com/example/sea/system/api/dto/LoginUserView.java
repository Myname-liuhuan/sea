package com.example.sea.system.api.dto;

import java.util.List;

import com.example.sea.common.security.entity.LoginUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LoginUser 的"对外视图"，剔除 password 字段。
 *
 * <p>用途：UI / sysUser/getLoginUser 走这个 DTO 返回，避免把 BCrypt 哈希泄漏给前端或非内部调用方。
 * <p>sea-auth 的 BCrypt 校验仍走 {@link LoginUser}（走 {@code /api/system/users/{username}/auth-info} 内部端点）。
 *
 * @author liuhuan
 * @date 2026-07-25
 */
@Data
@Schema(description = "登录用户视图（不含密码）")
public class LoginUserView {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "token 版本")
    private Long version;

    @Schema(description = "角色编码")
    private List<String> roles;

    @Schema(description = "权限点")
    private List<String> perms;

    @Schema(description = "是否需要强制改密")
    private Boolean requirePasswordChange;

    public static LoginUserView from(LoginUser u) {
        if (u == null) {
            return null;
        }
        LoginUserView v = new LoginUserView();
        v.setId(u.getId());
        v.setUsername(u.getUsername());
        v.setVersion(u.getVersion());
        v.setRoles(u.getRoles());
        v.setPerms(u.getPerms());
        v.setRequirePasswordChange(u.getRequirePasswordChange());
        return v;
    }
}
