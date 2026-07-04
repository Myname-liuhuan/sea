package com.example.sea.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 投递日志实体。
 *
 * <p>status: PENDING / SUCCESS / FAILED；retry 由定时任务扫 FAILED + attempts<3。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Accessors(chain = true)
@TableName("notify_log")
public class NotifyLogPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 业务键（taskNo） */
    private String bizKey;

    /** 通道 */
    private String channel;

    /** 接收方（手机号 / 邮箱） */
    private String receiver;

    /** 目标用户 */
    private Long userId;

    /** 模板编码 */
    private String templateCode;

    /** 负载密文（参数替换后的最终内容） */
    private String payloadCipher;

    /** PENDING / SUCCESS / FAILED */
    private String status;

    /** 错误 */
    private String error;

    /** 已重试次数 */
    private Integer attempts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
