package com.example.sea.notification.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.api.vo.InAppMessageVO;
import com.example.sea.notification.service.IInAppMessageService;
import com.example.sea.notification.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知服务对外 API。
 *
 * <p>sea-workflow 与前端都用得到：sea-workflow 通过 Feign 调
 * {@code /api/notification/send} / {@code /in-app}；前端通过
 * {@code /messages} / {@code /unread-count} / {@code /read}。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Tag(name = "通知服务")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final IInAppMessageService inAppMessageService;

    /**
     * 服务间调用（Feign 入口）。无强制权限校验，由调用方上行 token 携带权限。
     */
    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "通用通知发送（in-app → email → sms 降级）")
    public NotifyResult send(@RequestBody @Valid NotifyRequest request) {
        return notificationService.send(request);
    }

    @PostMapping("/in-app")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "仅发站内信")
    public NotifyResult sendInApp(@RequestBody @Valid NotifyRequest request) {
        NotifyRequest r = new NotifyRequest();
        r.setPrimaryChannel("IN_APP");
        r.setFallbackChannels(List.of("EMAIL", "SMS"));
        r.setReceiverUserId(request.getReceiverUserId());
        r.setTemplateCode(request.getTemplateCode());
        r.setParams(request.getParams());
        r.setBizKey(request.getBizKey());
        r.setAppName(request.getAppName());
        return notificationService.send(r);
    }

    /**
     * 前端铃铛用：未读数。
     */
    @PostMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "未读数")
    public CommonResult<Long> unreadCount() {
        Long me = SecurityContextUtil.getUserId();
        if (me == null) return CommonResult.failed("未登录");
        return inAppMessageService.unreadCount(me);
    }

    /**
     * 前端铃铛下拉：当前用户最近站内信列表。
     */
    @GetMapping("/messages")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "我的站内信列表")
    public CommonResult<PageResult<InAppMessageVO>> myMessages(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize) {
        Long me = SecurityContextUtil.getUserId();
        if (me == null) return CommonResult.failed("未登录");
        return inAppMessageService.myInbox(me, pageNum, pageSize);
    }

    @PostMapping("/messages/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "标记一条已读")
    public CommonResult<Void> markRead(@PathVariable Long id) {
        Long me = SecurityContextUtil.getUserId();
        if (me == null) return CommonResult.failed("未登录");
        return inAppMessageService.markRead(me, id);
    }

    @PostMapping("/messages/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "标记全部已读")
    public CommonResult<Void> markAllRead() {
        Long me = SecurityContextUtil.getUserId();
        if (me == null) return CommonResult.failed("未登录");
        return inAppMessageService.markAllRead(me);
    }
}
