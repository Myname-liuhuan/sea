package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.dto.ResetPasswordRequest;
import com.example.sea.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * <p>本控制器仅供其他微服务（sea-workflow）走 Feign 调用，<b>不</b>挂在
 * sys_menu 上，也不通过任何 v-hasPermi 控制（service 层也不依赖 SecurityContextUtil）。
 *
 * <p>网关层只需在路由白名单透传鉴权；同一 token 应能访问。
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
    @Operation(summary = "取用户字段（不含密码）")
    public CommonResult<Map<String, Object>> getRaw(@PathVariable Long id) {
        return sysUserService.getUserRaw(id);
    }

    @GetMapping("/{id}/leader-id")
    @Operation(summary = "取直属上级 user_id")
    public CommonResult<Long> getLeaderId(@PathVariable Long id) {
        return sysUserService.getUserLeaderId(id);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@PathVariable Long id,
                                               @RequestParam(value = "requirePasswordChange", required = false, defaultValue = "true") Boolean requirePasswordChange,
                                               @RequestBody ResetPasswordRequest body) {
        return sysUserService.resetPassword(id, body.getNewPassword(), requirePasswordChange);
    }
}
