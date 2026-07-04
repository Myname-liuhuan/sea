package com.example.sea.notification.notifier;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.notification.api.dto.NotifyRequest;
import com.example.sea.notification.api.dto.NotifyResult;
import com.example.sea.notification.constants.ChannelEnum;
import com.example.sea.notification.dao.NotifyLogMapper;
import com.example.sea.notification.dao.NotifyTemplateMapper;
import com.example.sea.notification.entity.NotifyLogPO;
import com.example.sea.notification.entity.NotifyTemplatePO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 阿里云短信实现。
 *
 * <p>vendor 配置项由 Nacos 的 {@code sea-notification.yaml} 提供：
 * <pre>
 * aliyun.sms.enabled=true
 * aliyun.sms.access-key-id=...
 * aliyun.sms.access-key-secret=...
 * aliyun.sms.sign-name=海纳
 * </pre>
 *
 * <p>vendor 配置缺失时 {@link #enabled(NotifyRequest)} 返回 false，主调度会跳过 SMS。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotifier implements Notifier {

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;

    @Value("${aliyun.sms.enabled:false}")
    private boolean smaEnabled;

    @Value("${aliyun.sms.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name:}")
    private String signName;

    private Client aliyunClient;

    @PostConstruct
    void init() {
        if (!smaEnabled || accessKeyId.isBlank() || accessKeySecret.isBlank()) {
            log.warn("SmsNotifier 未启用：缺少 vendor 配置");
            return;
        }
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            aliyunClient = new Client(config);
            log.info("SmsNotifier 已装载 aliyun sms client");
        } catch (Exception e) {
            log.error("SmsNotifier init failed", e);
            aliyunClient = null;
        }
    }

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.SMS;
    }

    @Override
    public boolean enabled(NotifyRequest request) {
        return smaEnabled && aliyunClient != null
                && request.getMobile() != null && !request.getMobile().isBlank();
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        if (!enabled(request)) return NotifyResult.failed(channel().getCode(), null, "SMS 未启用或无手机号");
        try {
            NotifyTemplatePO tpl = templateMapper.selectOne(
                    Wrappers.<NotifyTemplatePO>lambdaQuery()
                            .eq(NotifyTemplatePO::getTemplateCode, request.getTemplateCode())
                            .eq(NotifyTemplatePO::getChannel, channel().getCode())
                            .eq(NotifyTemplatePO::getEnabled, 1)
                            .orderByDesc(NotifyTemplatePO::getVersion)
                            .last("LIMIT 1"));
            if (tpl == null) return NotifyResult.failed(channel().getCode(), null, "短信模板不存在");
            String content = render(tpl.getContent(), request.getParams());
            String templateCode = request.getTemplateCode();

            SendSmsRequest req = new SendSmsRequest()
                    .setPhoneNumbers(request.getMobile())
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(toJsonString(request.getParams()));

            aliyunClient.sendSms(req);

            NotifyLogPO logPo = new NotifyLogPO();
            logPo.setBizKey(request.getBizKey());
            logPo.setChannel(channel().getCode());
            logPo.setReceiver(request.getMobile());
            logPo.setUserId(request.getReceiverUserId());
            logPo.setTemplateCode(request.getTemplateCode());
            logPo.setPayloadCipher(content);
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);
            return NotifyResult.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("SmsNotifier.send failed bizKey={}", request.getBizKey(), e);
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

    private String toJsonString(Map<String, String> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}
