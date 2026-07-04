package com.example.sea.notification.notifier;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.dao.NotifyTemplateMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.entity.NotifyTemplatePO;
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
 * <p>若用户的 email 为空则跳过（返回 success=false 但不阻塞主调度）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@example.com}")
    private String from;

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.EMAIL;
    }

    @Override
    public boolean enabled(NotifyRequest request) {
        return request.getEmail() != null && !request.getEmail().isBlank();
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        if (!enabled(request)) {
            return NotifyResult.failed(channel().getCode(), null, "用户无邮箱");
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
                return NotifyResult.failed(channel().getCode(), null, "邮件模板不存在");
            }
            String subject = render(tpl.getSubject(), request.getParams());
            String content = render(tpl.getContent(), request.getParams());

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
            logPo.setPayloadCipher(content);
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);
            return NotifyResult.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("EmailNotifier.send failed bizKey={}", request.getBizKey(), e);
            return NotifyResult.failed(channel().getCode(), null, e.getMessage());
        }
    }

    private String render(String tpl, Map<String, String> params) {
        if (tpl == null || params == null) return tpl;
        for (var e : params.entrySet()) {
            tpl = tpl.replace("${" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return tpl;
    }
}
