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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信实现（M5 默认 stub）。
 *
 * <p>vendor 配置项由 Nacos 的 {@code sea-notification.yaml} 提供：
 * <pre>
 * aliyun.sms.access-key-id=...
 * aliyun.sms.access-key-secret=...
 * aliyun.sms.sign-name=...
 * notify.channel.sms.enabled=true | false   # dev 默认 false
 * </pre>
 *
 * <p>{@code notify.channel.sms.enabled} 集中管理，由
 * {@link NotificationChannelProperties} 持有；凭据走原有 {@code @Value} 字段。
 * 修改 Nacos 上 {@code notify.channel.sms.enabled} 后无需重启即可生效。
 *
 * <p>本期实际 vendor SDK（{@code com.aliyun:aliyun-java-sdk-dysmsapi}）
 * 还没在 pom 里打入；接入时把下面的注释解开、再补 SendSmsRequest + Client 实现。
 * 当前 stub：在 enabled=true 时仅记录到 notify_log 并打印日志，不真发短信。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotifier implements Notifier {

    private final ObjectMapper REPLAY_MAPPER = new ObjectMapper();

    private final NotifyTemplateMapper templateMapper;
    private final NotifyLogMapper logMapper;
    private final NotificationChannelProperties channelProperties;

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
    public boolean enabled(NotifyDTO request) {
        // vendor 配置缺失或通道开关关闭时强制 disabled，避免无端发请求
        return channelProperties.getSms().getEnabled()
                && !accessKeyId.isBlank()
                && !accessKeySecret.isBlank()
                && request.getMobile() != null && !request.getMobile().isBlank();
    }

    @Override
    public NotifyVO send(NotifyDTO request) {
        if (!enabled(request)) {
            return NotifyVO.failed(channel().getCode(), null, "SMS 未启用或缺凭据");
        }
        try {
            NotifyTemplatePO tpl = templateMapper.selectOne(
                    Wrappers.<NotifyTemplatePO>lambdaQuery()
                            .eq(NotifyTemplatePO::getTemplateCode, request.getTemplateCode())
                            .eq(NotifyTemplatePO::getChannel, channel().getCode())
                            .eq(NotifyTemplatePO::getEnabled, 1)
                            .orderByDesc(NotifyTemplatePO::getVersion)
                            .last("LIMIT 1"));
            if (tpl == null) return NotifyVO.failed(channel().getCode(), null, "短信模板不存在");
            Map<String, String> renderParams = renderParams(request);
            String content = render(tpl.getContent(), renderParams);

            // TODO 接 vendor 后替换为真正的 aliyun client.sendSms
            log.warn("SmsNotifier.send STUB signName={} to={} content={}",
                    signName, request.getMobile(), content);

            NotifyLogPO logPo = new NotifyLogPO();
            logPo.setBizKey(request.getBizKey());
            logPo.setChannel(channel().getCode());
            logPo.setReceiver(request.getMobile());
            logPo.setUserId(request.getReceiverUserId());
            logPo.setTemplateCode(request.getTemplateCode());
            logPo.setPayloadCipher(buildReplayPayload(request));
            logPo.setStatus("SUCCESS");
            logPo.setAttempts(1);
            logMapper.insert(logPo);
            return NotifyVO.success(channel().getCode(), logPo.getId());
        } catch (Exception e) {
            log.error("SmsNotifier.send failed bizKey={}", request.getBizKey(), e);
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
            log.warn("SmsNotifier.buildReplayPayload failed bizKey={}", request.getBizKey(), e);
            return "{}";
        }
    }
}