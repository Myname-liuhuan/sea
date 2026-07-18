package com.example.sea.notification.notifier;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.notification.config.NotificationChannelProperties;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.InAppMessageMapper;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.dao.NotifyTemplateMapper;
import com.example.sea.notification.entity.InAppMessagePO;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.entity.NotifyTemplatePO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 站内信实现。
 *
 * <p>步骤：
 * <ol>
 *   <li>查模板</li>
 *   <li>渲染 content（占位替换，含 appName 顶层字段）</li>
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

    private final ObjectMapper REPLAY_MAPPER = new ObjectMapper();

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;
    private final InAppMessageMapper inAppMapper;
    private final NotificationChannelProperties channelProperties;

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.IN_APP;
    }

    @Override
    public boolean enabled(NotifyDTO request) {
        // 站内信是纯 DB 写入，唯一可控的是通道开关
        return channelProperties.getInapp().getEnabled();
    }

    @Override
    public NotifyVO send(NotifyDTO request) {
        if (!enabled(request)) {
            return NotifyVO.failed(channel().getCode(), null, "站内信通道关闭");
        }
        try {
            NotifyTemplatePO tpl = templateMapper.selectOne(
                    Wrappers.<NotifyTemplatePO>lambdaQuery()
                            .eq(NotifyTemplatePO::getTemplateCode, request.getTemplateCode())
                            .eq(NotifyTemplatePO::getChannel, channel().getCode())
                            .eq(NotifyTemplatePO::getEnabled, 1)
                            .orderByDesc(NotifyTemplatePO::getVersion)
                            .last("LIMIT 1"));
            if (tpl == null) {
                return NotifyVO.failed(channel().getCode(), null, "模板不存在: " + request.getTemplateCode());
            }
            Map<String, String> renderParams = renderParams(request);
            String title = render(tpl.getSubject(), renderParams);
            String content = render(tpl.getContent(), renderParams);

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
            logPo.setPayloadCipher(buildReplayPayload(request));
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);

            // TODO M3.C：WebSocket push 给该 user（由 InAppMessageService 维护 session）
            return NotifyVO.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("InAppNotifier.send failed bizKey={}", request.getBizKey(), e);
            return NotifyVO.failed(channel().getCode(), null, e.getMessage());
        }
    }

    /**
     * 构造模板渲染参数：用户传的 params + NotifyDTO.appName（顶层字段注入），
     * 这样模板里 ${appName} 占位符能渲染。
     */
    private Map<String, String> renderParams(NotifyDTO request) {
        Map<String, String> base = request.getParams() == null
                ? new HashMap<>()
                : new HashMap<>(request.getParams());
        if (request.getAppName() != null) {
            base.put("appName", request.getAppName());
        }
        return base;
    }

    private String render(String tpl, Map<String, String> params) {
        if (tpl == null || params == null) return tpl;
        for (var e : params.entrySet()) {
            tpl = tpl.replace("${" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return tpl;
    }

    /** §14 #11：直接把 NotifyDTO 序列化进 notify_log.payload_cipher，重试时反序列化即可重渲染。 */
    private String buildReplayPayload(NotifyDTO request) {
        try {
            return REPLAY_MAPPER.writeValueAsString(request);
        } catch (Exception e) {
            log.warn("InAppNotifier.buildReplayPayload failed bizKey={}", request.getBizKey(), e);
            return "{}";
        }
    }
}