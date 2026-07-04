package com.example.sea.notification.notifier;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.dao.NotifyTemplateMapper;
import com.example.sea.notification.entity.InAppMessagePO;
import com.example.sea.notification.dao.InAppMessageMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.entity.NotifyTemplatePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 站内信实现。
 *
 * <p>步骤：
 * <ol>
 *   <li>查模板</li>
 *   <li>渲染 content（占位替换）</li>
 *   <li>写 in_app_message 行</li>
 *   <li>落 notify_log（默认 SUCCESS）</li>
 *   <li>尝试 WS push 给该 user（由 M3.C 的 WebSocket Handler 提供，本期 hook 为空）</li>
 * </ol>
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InAppNotifier implements Notifier {

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;
    private final InAppMessageMapper inAppMapper;

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.IN_APP;
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        try {
            NotifyTemplatePO tpl = templateMapper.selectOne(
                    Wrappers.<NotifyTemplatePO>lambdaQuery()
                            .eq(NotifyTemplatePO::getTemplateCode, request.getTemplateCode())
                            .eq(NotifyTemplatePO::getChannel, channel().getCode())
                            .eq(NotifyTemplatePO::getEnabled, 1)
                            .orderByDesc(NotifyTemplatePO::getVersion)
                            .last("LIMIT 1"));
            if (tpl == null) {
                return NotifyResult.failed(channel().getCode(), null, "模板不存在: " + request.getTemplateCode());
            }
            String title = render(tpl.getSubject(), request.getParams());
            String content = render(tpl.getContent(), request.getParams());

            InAppMessagePO msg = new InAppMessagePO();
            msg.setUserId(request.getReceiverUserId());
            msg.setTitle(title);
            msg.setContent(content);
            msg.setLink("/workflow/detail/" + request.getBizKey());
            msg.setBizKey(request.getBizKey());
            msg.setReadFlag(0);
            inAppMapper.insert(msg);

            NotifyLogPO logPo = new NotifyLogPO();
            logPo.setBizKey(request.getBizKey());
            logPo.setChannel(channel().getCode());
            logPo.setReceiver(String.valueOf(request.getReceiverUserId()));
            logPo.setUserId(request.getReceiverUserId());
            logPo.setTemplateCode(request.getTemplateCode());
            logPo.setPayloadCipher(content);
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);

            // TODO M3.C：WebSocket push 给该 user（由 InAppMessageService 维护 session）
            return NotifyResult.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("InAppNotifier.send failed bizKey={}", request.getBizKey(), e);
            return NotifyResult.failed(channel().getCode(), null, e.getMessage());
        }
    }

    private String render(String tpl, java.util.Map<String, String> params) {
        if (tpl == null || params == null) return tpl;
        for (var e : params.entrySet()) {
            tpl = tpl.replace("${" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return tpl;
    }

    @SuppressWarnings("unused")
    private LambdaQueryWrapper<NotifyTemplatePO> empty() {
        return Wrappers.lambdaQuery();
    }
}
