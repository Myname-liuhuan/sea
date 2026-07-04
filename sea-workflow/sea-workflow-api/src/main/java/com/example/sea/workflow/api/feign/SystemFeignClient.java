package com.example.sea.workflow.api.feign;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.dto.ResetPasswordRequest;
import com.example.sea.workflow.api.feign.fallback.SystemFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 调 sea-system 的 Feign 客户端。
 *
 * <p>设计约束：
 * <ul>
 *   <li>{@link #getUserRaw(Long)} 用 {@link Map} 透传避免依赖 SysUserVO 跨服务反引</li>
 *   <li>{@link #getUserLeaderId(Long)} 仅取直属上级 ID，避免一次拉整个 user</li>
 * </ul>
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@FeignClient(value = "sea-system",
        contextId = "workflowSystemFeignClient",
        fallbackFactory = SystemFeignClientFallBack.class)
public interface SystemFeignClient {

    /**
     * 取用户简要信息（含 deptId、level、leaderId、email、mobile 等）。
     */
    @GetMapping("/api/system/users/{id}/raw")
    CommonResult<Map<String, Object>> getUserRaw(@PathVariable("id") Long userId);

    /**
     * 取直属上级 user_id。无上级返 null。
     */
    @GetMapping("/api/system/users/{id}/leader-id")
    CommonResult<Long> getUserLeaderId(@PathVariable("id") Long userId);

    /**
     * 重置密码（明文 + requirePasswordChange）。
     */
    @PostMapping("/api/system/users/{id}/reset-password")
    CommonResult<Void> resetPassword(@PathVariable("id") Long userId,
                                     @RequestParam(value = "requirePasswordChange", required = false) Boolean requirePasswordChange,
                                     @RequestBody ResetPasswordRequest body);
}
