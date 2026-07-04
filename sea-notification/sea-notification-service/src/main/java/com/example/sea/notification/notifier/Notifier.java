package com.example.sea.notification.notifier;

import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.constants.ChannelEnum;

/**
 * 通知通道接口，由具体实现（Email / Sms / InApp）实现。
 *
 * <p>实现需要做：
 * <ul>
 *   <li>模版渲染（占位 ${name} 替换）</li>
 *   <li>通讯（JavaMail / Aliyun SDK / InApp DB）</li>
 *   <li>异常吃掉上层降级链</li>
 * </ul>
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface Notifier {

    /** 该实现支持的通道 */
    ChannelEnum channel();

    /** 渲染模板并发送；返回结果（success=false 表示失败但不必抛异常） */
    NotifyResult send(NotifyRequest request);

    /**
     * 返回实现层是否实际启用（如 SMS 在某环境 vendorKey 缺失，应返回 false 让主调度跳过）。
     */
    default boolean enabled(NotifyRequest request) {
        return true;
    }
}
