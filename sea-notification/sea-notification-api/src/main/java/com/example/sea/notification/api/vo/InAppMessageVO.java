package com.example.sea.notification.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内信视图（铃铛未读与列表展示）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "站内信视图")
public class InAppMessageVO {

    private Long id;

    @Schema(description = "收信人 user_id")
    private Long userId;

    private String title;
    private String content;
    private String link;

    @Schema(description = "0 未读 1 已读")
    private Integer readFlag;

    private LocalDateTime createdAt;
}
