package com.example.sea.workflow.util;

import com.example.sea.notification.api.dto.NotifyDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sea-workflow → sea-notification 入参构造器。
 *
 * <p>统一处理密码重置场景（IN_APP 主通道 + EMAIL/SMS 降级链），
 * 把 PasswordResetDelegate 与 WorkflowAdminServiceImpl 重复的手工 HashMap 装配
 * 收敛到一处，避免字段名漂移导致 Feign 调用反序列化失败。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public final class NotifyPayloadBuilder {

    /** 应用名固定为 "海纳系统"，模板里 ${appName} 用。 */
    public static final String APP_NAME = "海纳系统";

    private NotifyPayloadBuilder() {
    }

    /**
     * PWD_RESET_OK 模板通用入参。
     *
     * @param receiverUserId 通知接收人
     * @param email          邮箱（EMAIL 通道用，可空）
     * @param mobile         手机号（SMS 通道用，可空）
     * @param tempPassword   重置后的临时密码
     * @param approver       审批人角色标签（"审批人" / "管理员"），写入模板的 approver 占位符
     * @param bizKey         业务键（工单号 / "ADMIN_{userId}" 等）
     */
    public static NotifyDTO buildPasswordResetPayload(Long receiverUserId,
                                                     String email,
                                                     String mobile,
                                                     String tempPassword,
                                                     String approver,
                                                     String bizKey) {
        Map<String, String> params = new HashMap<>();
        params.put("pwd", tempPassword);
        params.put("approver", approver);
        params.put("appName", APP_NAME);

        NotifyDTO dto = new NotifyDTO();
        dto.setPrimaryChannel("IN_APP");
        dto.setFallbackChannels(List.of("EMAIL", "SMS"));
        dto.setReceiverUserId(receiverUserId);
        dto.setEmail(email);
        dto.setMobile(mobile);
        dto.setTemplateCode("PWD_RESET_OK");
        dto.setParams(params);
        dto.setBizKey(bizKey);
        dto.setAppName(APP_NAME);
        return dto;
    }
}