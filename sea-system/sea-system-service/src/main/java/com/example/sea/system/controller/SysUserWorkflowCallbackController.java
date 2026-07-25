package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.api.constants.PermissionConstants;
import com.example.sea.system.api.dto.ResetPasswordRequest;
import com.example.sea.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户服务对内的"流程回调用"接口。
 *
 * <p><b>权限策略</b>：仅持有 {@code internal:callback} 权限的调用方能访问。
 * 业务用户（含 admin）默认无此权限，避免内部接口被外部滥用。
 *
 * <p>调用方（sea-workflow 的 {@code SystemFeignClient}）走 Nacos 共享配置
 * {@code feign.internal.token} 注入 token，token 内 authorities 已包含此权限。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Tag(name = "用户流程回调（内部）")
@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class SysUserWorkflowCallbackController {

    private final ISysUserService sysUserService;

    @GetMapping("/{id}/raw")
    @PreAuthorize("hasAuthority('" + PermissionConstants.INTERNAL_CALLBACK + "')")
    @Operation(summary = "取用户字段（不含密码）")
    public CommonResult<Map<String, Object>> getRaw(@PathVariable Long id) {
        return sysUserService.getUserRaw(id);
    }

    @GetMapping("/{id}/leader-id")
    @PreAuthorize("hasAuthority('" + PermissionConstants.INTERNAL_CALLBACK + "')")
    @Operation(summary = "取直属上级 user_id")
    public CommonResult<Long> getLeaderId(@PathVariable Long id) {
        return sysUserService.getUserLeaderId(id);
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('" + PermissionConstants.INTERNAL_CALLBACK + "')")
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@PathVariable Long id,
                                               @RequestParam(value = "requirePasswordChange", required = false, defaultValue = "true") Boolean requirePasswordChange,
                                               @RequestBody ResetPasswordRequest body) {
        return sysUserService.resetPassword(id, body.getNewPassword(), requirePasswordChange);
    }

    /**
     * 鉴权专用：含 password hash，仅供 sea-auth 走 Feign 内部调用。
     *
     * <p>路径与 callback 其他端点平级（都在 /api/system/users/ 下），权限同样要求 internal:callback。
     * 业务用户（admin / 其他人）无 internal:callback 权限，无法调到这里——彻底阻断 BCrypt 哈希外泄。
     */
    @GetMapping("/{username}/auth-info")
    @PreAuthorize("hasAuthority('" + PermissionConstants.INTERNAL_CALLBACK + "')")
    @Operation(summary = "取鉴权用 LoginUser（含 password）")
    public CommonResult<LoginUser> getAuthLoginUser(@PathVariable String username) {
        return sysUserService.getAuthLoginUser(username);
    }
}
