package com.example.sea.notification.service;

import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;

/**
 * 通知主调度。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface INotificationService {

    /**
     * 发送通知（按主通道 → 降级链）。
     *
     * <p>语义：成功返回 success=true；全部失败返回 success=false 并最终落 notify_log.FAILED。
     * 本方法的失败不会抛异常（避免触发业务侧事务回滚，由调用方决定是否上抛）。
     */
    NotifyResult send(NotifyRequest request);
}
