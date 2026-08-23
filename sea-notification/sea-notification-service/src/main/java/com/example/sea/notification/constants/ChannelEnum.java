package com.example.sea.notification.constants;

import lombok.Getter;

/**
 * 通知通道枚举。
 *
 * <p>优先级 inApp → email → sms（设计文档 §4 sea-notification）。
 */
@Getter
public enum ChannelEnum {

    /** 站内信（必达） */
    IN_APP("IN_APP", "站内信"),
    /** 邮件 */
    EMAIL("EMAIL", "邮件"),
    /** 短信 */
    SMS("SMS", "短信");

    private final String code;
    private final String label;

    ChannelEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ChannelEnum of(String code) {
        if (code == null) return null;
        for (ChannelEnum c : values()) {
            if (c.code.equalsIgnoreCase(code)) return c;
        }
        return null;
    }
}
