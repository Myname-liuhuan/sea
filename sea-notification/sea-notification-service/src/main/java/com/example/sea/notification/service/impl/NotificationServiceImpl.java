package com.example.sea.notification.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.notifier.Notifier;
import com.example.sea.notification.service.INotificationService;
import com.example.sea.notification.constants.ChannelEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 主调度：按主通道 + 降级链依次尝试。
 *
 * <p>行为：
 * <ol>
 *   <li>主通道 enabled → send，success=true 立即返回</li>
 *   <li>主通道失败 / 未启用 → fallbackChannels 顺序尝试</li>
 *   <li>全部失败 → 落 notify_log.FAILED</li>
 * </ol>
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final List<Notifier> notifiers;
    private final NotifyLogMapper logMapper;

    @Async
    @Override
    public CommonResult<NotifyVO> send(NotifyDTO request) {
        Set<String> tried = new LinkedHashSet<>();
        tried.add(request.getPrimaryChannel());
        if (request.getFallbackChannels() != null) tried.addAll(request.getFallbackChannels());

        Long lastLogId = null;
        for (String channelCode : tried) {
            ChannelEnum ch = ChannelEnum.of(channelCode);
            if (ch == null) continue;
            Notifier n = pickNotifier(ch);
            if (n == null || !n.enabled(request)) continue;

            NotifyVO r = n.send(request);
            if (r != null && r.isSuccess()) {
                if (r.getLogId() != null) lastLogId = r.getLogId();
                log.info("notify.send ok channel={} bizKey={}", ch, request.getBizKey());
                return CommonResult.success(r);
            }
            if (r != null) log.warn("notify.send fail channel={} bizKey={} err={}",
                    ch, request.getBizKey(), r.getError());
        }

        // 全失败：写一条 FAILED
        NotifyLogPO logPo = new NotifyLogPO();
        logPo.setBizKey(request.getBizKey());
        logPo.setChannel(request.getPrimaryChannel());
        logPo.setReceiver(request.getMobile() != null ? request.getMobile() : request.getEmail());
        logPo.setUserId(request.getReceiverUserId());
        logPo.setTemplateCode(request.getTemplateCode());
        logPo.setStatus("FAILED");
        logPo.setError("全部通道失败");
        logPo.setAttempts(tried.size());
        logMapper.insert(logPo);
        lastLogId = logPo.getId();
        log.warn("notify.send all-failed bizKey={} tried={}", request.getBizKey(), tried);
        return CommonResult.success(NotifyVO.failed(request.getPrimaryChannel(), lastLogId, "全部通道失败"));
    }

    private Notifier pickNotifier(ChannelEnum ch) {
        if (notifiers == null) return null;
        for (Notifier n : notifiers) {
            if (n.channel() == ch) return n;
        }
        return null;
    }

    @SuppressWarnings("unused")
    private List<String> safeList(List<String> in) {
        return in == null ? new ArrayList<>() : in;
    }
}