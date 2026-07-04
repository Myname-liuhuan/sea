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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信实现（M5 默认 stub）。
 *
 * <p>vendor 配置项由 Nacos 的 {@code sea-notification.yaml} 提供：
 * <pre>
 * aliyun.sms.enabled=false  # dev 默认关闭
 * aliyun.sms.access-key-id=...
 * aliyun.sms.access-key-secret=...
 * aliyun.sms.sign-name=...
 * </pre>
 *
 * <p>本期实际 vendor SDK（{@code com.aliyun:aliyun-java-sdk-dysmsapi}）
 * 还没在 pom 里打入；接入时把上面的注释解开、再补 SendSmsRequest + Client 实现。
 * 当前 stub：在 enabled=true 时仅记录到 notify_log 并打印日志，不真发短信。
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

    @Override
    public ChannelEnum channel() {
        return ChannelEnum.SMS;
    }

    @Override
    public boolean enabled(NotifyRequest request) {
        // vendor 配置缺失时强制 disabled，避免无端发请求
        return smaEnabled && !accessKeyId.isBlank() && !accessKeySecret.isBlank()
                && request.getMobile() != null && !request.getMobile().isBlank();
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        if (!enabled(request)) {
            return NotifyResult.failed(channel().getCode(), null, "SMS 未启用或缺凭据");
        }
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

            // TODO 接 vendor 后替换为真正的 aliyun client.sendSms
            log.warn("SmsNotifier.send STUB signName={} to={} content={}",
                    signName, request.getMobile(), content);

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
}
