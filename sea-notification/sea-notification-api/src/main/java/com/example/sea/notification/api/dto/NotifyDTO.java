package com.example.sea.notification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 通用通知请求（in-app → email → sms 降级）。
 *
 * <p>同时承担前端入参与 Feign 入参；作为 Feign 调用 payload 时，
 * sea-workflow 通过 {@code NotifyPayloadBuilder} 构造后传入。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "通用通知请求")
public class NotifyDTO {

    @Schema(description = "主通道，必须填")
    @NotBlank
    private String primaryChannel;

    @Schema(description = "降级通道（按顺序试）")
    private List<String> fallbackChannels;

    @Schema(description = "目标用户 ID（必填，便于站内信与日志绑定）")
    @NotNull
    private Long receiverUserId;

    @Schema(description = "手机号（SMS 通道需要，目标用户无手机号时跳过）")
    private String mobile;

    @Schema(description = "邮箱（EMAIL 通道需要）")
    private String email;

    @Schema(description = "模板编码 e.g. PWD_RESET_OK")
    @NotBlank
    private String templateCode;

    @Schema(description = "模板渲染参数 map")
    @NotEmpty
    private Map<String, String> params;

    @Schema(description = "业务键（如 taskNo），便于日志聚合")
    private String bizKey;

    @Schema(description = "应用名（注入到模板）")
    private String appName;
}