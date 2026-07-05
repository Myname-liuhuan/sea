package com.example.sea.notification.notifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 通知投递参数快照，用于重试时重新渲染模板。
 *
 * <p>每个 Notifier 把这个对象 JSON 序列化后写入 notify_log.payload_cipher；
 * NotifyRetryJob 反序列化后走完整渲染路径（不用 fallback 占位符）。
 *
 * <p>§14 #11：邮件失败后可基于模板 + params 重发，不再走 ${payload} 占位补救。
 *
 * @author liuhuan
 * @date 2026-07-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyReplayPayload {

    /** 通道：IN_APP / EMAIL / SMS */
    private String channel;

    /** 收件人 userId */
    private Long receiverUserId;

    /** 邮件地址（EMAIL 通道使用） */
    private String email;

    /** 手机号（SMS 通道使用） */
    private String mobile;

    /** 模板编码 */
    private String templateCode;

    /** 模板参数 */
    private Map<String, String> params;

    /** 业务键 */
    private String bizKey;

    /** 应用名（注入模板） */
    private String appName;
}
