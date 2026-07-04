package com.example.sea.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 审批记录实体。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Accessors(chain = true)
@TableName("workflow_approval")
public class WorkflowApprovalPO {

    /** 主键（MP 默认雪花策略） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 工单 ID */
    private Long taskId;

    /** 节点标识 dept_leader / hr */
    private String nodeKey;

    /** 审批层级（第几级） */
    private Integer nodeOrder;

    /** 实际操作人 user_id */
    private Long approverId;

    /** 1 通过 0 拒绝 */
    private Integer approved;

    /** 意见 */
    private String comment;

    /** 转交来源 */
    private Long delegatedFrom;

    /** 创建时间 */
    private LocalDateTime createTime;
}
