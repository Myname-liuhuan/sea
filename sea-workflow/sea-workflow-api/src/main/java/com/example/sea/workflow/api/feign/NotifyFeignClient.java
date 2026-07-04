package com.example.sea.workflow.api.feign;

import com.example.sea.workflow.api.feign.fallback.NotifyFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

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
     * @param payload 业务侧组装：templateCode / channels / receiverUserId / params / bizKey
     */
    @PostMapping("/api/notification/send")
    Map<String, Object> send(@RequestBody Map<String, Object> payload);

    /**
     * 仅发站内信（用于工单状态变更提醒）。
     */
    @PostMapping("/api/notification/in-app")
    Map<String, Object> sendInApp(@RequestBody Map<String, Object> payload);

    /**
     * 收件箱未读数（前端铃铛调用）。
     */
    @PostMapping("/api/notification/unread-count")
    Long unreadCount(@RequestBody List<Long> userIds);
}
