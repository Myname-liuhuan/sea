package com.example.sea.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 通知通道总开关属性。
 *
 * <p>配置前缀 {@code notify.channel}，每个通道一个 {@link ChannelConfig}，
 * 由 {@link #isSmsEnabled()} / {@link #isEmailEnabled()} / {@link #isInappEnabled()}
 * 等交给对应 Notifier 的运行期 {@code enabled()} 判断使用。
 *
 * <p>{@link RefreshScope} 配合 Nacos {@code refresh-enabled=true}，
 * 修改 Nacos 配置后无需重启即可热切换。
 *
 * <p>默认：
 * <ul>
 *   <li>sms = false（dev 默认关，依赖 vendor 凭据）</li>
 *   <li>email / inapp = true</li>
 * </ul>
 *
 * @author liuhuan
 * @date 2026-07-18
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "notify.channel")
public class NotificationChannelProperties {

    /** 短信通道配置 */
    private ChannelConfig sms = new ChannelConfig(false);

    /** 邮件通道配置 */
    private ChannelConfig email = new ChannelConfig(true);

    /** 站内信通道配置 */
    private ChannelConfig inapp = new ChannelConfig(true);

    @Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ChannelConfig {

        /** 是否启用该通道 */
        private Boolean enabled = true;
    }
}