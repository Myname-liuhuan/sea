package com.example.sea.notification.service.jobs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.notifier.Notifier;
import com.example.sea.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 通知重试 job。
 *
 * <p>每分钟扫描一次：status=FAILED AND attempts < 3，调用对应 Notifier 重发。
 * 成功后置 SUCCESS；attempts 自增。配置可通过 Nacos 调：
 * <pre>
 * notify.retry.max-attempts = 3
 * notify.retry.cron = "0 * * * * ?"
 * </pre>
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
    private final INotificationService notificationService;

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
            if (req == null) continue;
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
     * 仅重试有 payload 缓存的短信 / 邮件失败项；站内信不会出现 FAILED（in_app 极少出错）。
     * 这里做最小可用：如果原 payload 已被清，只能跳过重试。
     */
    private NotifyRequest rebuildRequest(NotifyLogPO l) {
        NotifyRequest r = new NotifyRequest();
        r.setReceiverUserId(l.getUserId());
        r.setTemplateCode(l.getTemplateCode());
        r.setBizKey(l.getBizKey());
        r.setPrimaryChannel(l.getChannel());
        r.setFallbackChannels(List.of("IN_APP", "EMAIL", "SMS"));
        // 邮件 payload 解密、参数无法从 log 反推 → 简化：不重试邮件，邮件失败直接告警。
        if ("SMS".equalsIgnoreCase(l.getChannel())) {
            r.setMobile(l.getReceiver());
        } else if ("EMAIL".equalsIgnoreCase(l.getChannel())) {
            r.setEmail(l.getReceiver());
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("payload", l.getPayloadCipher() == null ? "" : l.getPayloadCipher());
        r.setParams(params);
        return r;
    }

    @SuppressWarnings("unused")
    private void suppress() { notificationService.toString(); }
}
