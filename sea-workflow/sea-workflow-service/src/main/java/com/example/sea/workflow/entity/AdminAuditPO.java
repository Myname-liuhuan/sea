package com.example.sea.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 管理员紧急操作审计实体。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Accessors(chain = true)
@TableName("admin_audit")
public class AdminAuditPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 操作人 user_id */
    private Long operatorId;

    /** 动作，例如 EMERGENCY_RESET */
    private String action;

    /** 目标用户 */
    private Long targetUserId;

    /** 操作原因 */
    private String reason;

    /** 操作人 IP */
    private String requestIp;

    private LocalDateTime createdTime;
}
