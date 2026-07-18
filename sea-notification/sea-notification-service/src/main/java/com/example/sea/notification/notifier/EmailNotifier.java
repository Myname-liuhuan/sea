package com.example.sea.notification.notifier;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.notification.api.vo.NotifyVO;
import com.example.sea.notification.config.NotificationChannelProperties;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.dao.NotifyTemplateMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.entity.NotifyTemplatePO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 邮件实现：JavaMailSender。
 *
 * <p>开关由 {@link NotificationChannelProperties} 的
 * {@code notify.channel.email.enabled} 集中管理，热切由 {@code @RefreshScope} 保证。
 *
 * <p>若用户的 email 为空则跳过（返回 success=false 但不阻塞主调度）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    private final ObjectMapper REPLAY_MAPPER = new ObjectMapper();

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;
    private final JavaMailSender mailSender;
    private final NotificationChannelProperties channelProperties;

    @Value("${spring.mail.username:noreply@example.com}")
    private String from;

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.EMAIL;
    }

    @Override
    public boolean enabled(NotifyDTO request) {
        return channelProperties.getEmail().getEnabled()
                && request.getEmail() != null && !request.getEmail().isBlank();
    }

    @Override
    public NotifyVO send(NotifyDTO request) {
        if (!enabled(request)) {
            return NotifyVO.failed(channel().getCode(), null, "邮件通道关闭或用户无邮箱");
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
                return NotifyVO.failed(channel().getCode(), null, "邮件模板不存在");
            }
            Map<String, String> renderParams = renderParams(request);
            String subject = render(tpl.getSubject(), renderParams);
            String content = render(tpl.getContent(), renderParams);

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(request.getEmail());
            msg.setSubject(subject);
            msg.setText(content);
            mailSender.send(msg);

            NotifyLogPO logPo = new NotifyLogPO();
            logPo.setBizKey(request.getBizKey());
            logPo.setChannel(channel().getCode());
            logPo.setReceiver(request.getEmail());
            logPo.setUserId(request.getReceiverUserId());
            logPo.setTemplateCode(request.getTemplateCode());
            logPo.setPayloadCipher(buildReplayPayload(request));
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);
            return NotifyVO.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("EmailNotifier.send failed bizKey={}", request.getBizKey(), e);
            return NotifyVO.failed(channel().getCode(), null, e.getMessage());
        }
    }

    /**
     * 构造模板渲染参数：用户传的 params + NotifyDTO.appName（顶层字段注入），
     * 这样模板里 ${appName} 占位符能渲染。
     */
    private Map<String, String> renderParams(NotifyDTO request) {
        Map<String, String> base = request.getParams() == null
                ? new java.util.HashMap<>()
                : new java.util.HashMap<>(request.getParams());
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
            log.warn("EmailNotifier.buildReplayPayload failed bizKey={}", request.getBizKey(), e);
            return "{}";
        }
    }
}