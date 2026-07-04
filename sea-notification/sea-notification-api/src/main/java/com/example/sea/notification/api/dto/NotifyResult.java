package com.example.sea.notification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知发送结果。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "通知发送结果")
public class NotifyResult {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "最终成达通道")
    private String channel;

    @Schema(description = "通知日志 ID")
    private Long logId;

    @Schema(description = "错误描述（成功为空）")
    private String error;

    public static NotifyResult success(String channel, Long logId) {
        NotifyResult r = new NotifyResult();
        r.success = true;
        r.channel = channel;
        r.logId = logId;
        return r;
    }

    public static NotifyResult failed(String channel, Long logId, String error) {
        NotifyResult r = new NotifyResult();
        r.success = false;
        r.channel = channel;
        r.logId = logId;
        r.error = error;
        return r;
    }
}
