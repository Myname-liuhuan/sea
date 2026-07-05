package com.example.sea.notification.service.jobs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.notifier.Notifier;
import com.example.sea.notification.notifier.NotifyReplayPayload;
import com.example.sea.notification.service.INotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知重试 job。
 *
 * <p>每分钟扫描一次：status=FAILED AND attempts < maxAttempts，调用对应 Notifier 重发。
 * 成功后置 SUCCESS；attempts 自增。
 *
 * <p>§14 #11：payload_cipher 里是 NotifyReplayPayload JSON 序列化；
 * 反序列化得到完整 NotifyRequest 后直接发给对应 Notifier，让模板重新渲染，
 * 不再依赖 ${payload} 占位补救。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyRetryJob {

    @Value("${notify.retry.max-attempts:3}")
    private int maxAttempts;

    private final NotifyLogMapper logMapper;
    private final List<Notifier> notifiers;

    @Scheduled(cron = "0 * * * * ?")
    public void retryFailed() {
        LambdaQueryWrapper<NotifyLogPO> w = Wrappers.<NotifyLogPO>lambdaQuery()
                .eq(NotifyLogPO::getStatus, "FAILED")
                .lt(NotifyLogPO::getAttempts, maxAttempts)
                .orderByAsc(NotifyLogPO::getCreatedAt)
                .last("LIMIT 50");

        List<NotifyLogPO> list = logMapper.selectList(w);
        if (list.isEmpty()) return;
        log.info("notify.retry pick count={}", list.size());

        for (NotifyLogPO l : list) {
            Notifier n = pickNotifier(l.getChannel());
            if (n == null) continue;
            NotifyRequest req = rebuildRequest(l);
            if (req == null) {
                log.warn("notify.retry skip logId={} reason=payload-unparseable", l.getId());
                continue;
            }
            NotifyResult r = n.send(req);
            if (r != null && r.isSuccess()) {
                l.setStatus("SUCCESS");
                l.setAttempts(l.getAttempts() + 1);
                l.setError(null);
            } else {
                l.setAttempts(l.getAttempts() + 1);
                l.setError(r == null ? "null-result" : r.getError());
            }
            logMapper.updateById(l);
        }
    }

    private Notifier pickNotifier(String code) {
        for (Notifier n : notifiers) {
            if (n.channel().getCode().equalsIgnoreCase(code)) return n;
        }
        return null;
    }

    /**
     * §14 #11：从 notify_log.payload_cipher 反序列化 NotifyReplayPayload，
     * 转化为完整 NotifyRequest 返回。失败时返 null。
     */
    private NotifyRequest rebuildRequest(NotifyLogPO l) {
        if (l.getPayloadCipher() == null || l.getPayloadCipher().isBlank()) return null;
        try {
            ObjectMapper m = new ObjectMapper();
            NotifyReplayPayload p = m.readValue(l.getPayloadCipher(), NotifyReplayPayload.class);
            NotifyRequest r = new NotifyRequest();
            r.setPrimaryChannel(p.getChannel());
            r.setFallbackChannels(List.of("IN_APP", "EMAIL", "SMS"));
            r.setReceiverUserId(p.getReceiverUserId());
            r.setEmail(p.getEmail());
            r.setMobile(p.getMobile());
            r.setTemplateCode(p.getTemplateCode());
            r.setParams(p.getParams());
            r.setBizKey(p.getBizKey());
            r.setAppName(p.getAppName());
            return r;
        } catch (Exception e) {
            log.warn("notify.retry.parse failed logId={} cause={}", l.getId(), e.getMessage());
            return null;
        }
    }

    /** 抑制：保留 INotificationService 依赖便于测试期健康监测。 */
    @SuppressWarnings("unused")
    private final INotificationService notificationService;
}
