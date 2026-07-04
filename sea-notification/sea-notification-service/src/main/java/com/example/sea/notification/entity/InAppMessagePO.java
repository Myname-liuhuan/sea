package com.example.sea.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 站内信实体（铃铛未读源）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Accessors(chain = true)
@TableName("in_app_message")
public class InAppMessagePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private String link;
    private String bizKey;

    /** 0 未读 1 已读 */
    private Integer readFlag;

    private LocalDateTime createdAt;
}
