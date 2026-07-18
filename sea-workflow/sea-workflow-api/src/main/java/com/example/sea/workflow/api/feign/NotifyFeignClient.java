package com.example.sea.workflow.api.feign;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.workflow.api.feign.fallback.NotifyFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调 sea-notification 的 Feign 客户端。
 *
 * <p>M2 由 PasswordResetDelegate stub 调用，M3 接入。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@FeignClient(value = "sea-notification",
        contextId = "workflowNotifyFeignClient",
        fallbackFactory = NotifyFeignClientFallBack.class)
public interface NotifyFeignClient {

    /**
     * 通用三通道发送（inApp → email → sms 降级）。
     *
     * @param request 业务侧组装：templateCode / channels / receiverUserId / params / bizKey
     */
    @PostMapping("/send")
    CommonResult<NotifyVO> send(@RequestBody NotifyDTO request);

    /**
     * 收件箱未读数。当前登录人 user_id 由 server 端从 SecurityContext 取，
     * 入参仅作为"查谁的"的可选覆盖（默认取当前登录人）。
     */
    @GetMapping("/unread-count")
    CommonResult<Long> unreadCount(@RequestParam(value = "userId", required = false) Long userId);
}