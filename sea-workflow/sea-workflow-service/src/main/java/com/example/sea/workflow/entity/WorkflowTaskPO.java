package com.example.sea.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 工单主表实体。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("workflow_task")
public class WorkflowTaskPO extends BaseEntity {

    /** 主键（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 工单编号（业务可见） */
    private String taskNo;

    /** 业务类型，当前固定 PASSWORD_RESET */
    private String businessType;

    /** 业务关联键（可选） */
    private String businessKey;

    /** 申请人 user_id */
    private Long applicantId;

    /** 重置目标 user_id */
    private Long targetUserId;

    /** 申请原因 */
    private String reason;

    /** 紧急程度：1 普通 / 2 紧急 */
    private Integer urgency;

    /**
     * 状态：0 待审批 1 审批中 2 已通过 3 已拒绝 4 已撤回 5 已完成
     * 详 WorkflowStatusEnum
     */
    private Integer status;

    /** Flowable 流程实例 ID */
    private String flowInstanceId;

    /** 当前等待节点（便于列表展示） */
    private String currentNode;

    /** 临时密码 AES 密文（admin 可见） */
    private String temporaryPasswordCipher;

    /** 乐观锁 */
    private Integer version;
}
