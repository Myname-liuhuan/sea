package com.example.sea.workflow.constants;

/**
 * 工单状态枚举。
 *
 * <p>对应 workflow_task.status 字段。
 */
public enum WorkflowStatusEnum {

    /** 待审批（尚未流转到第一个审批节点） */
    PENDING(0, "待审批"),

    /** 审批中（已流转到至少一个审批节点） */
    IN_PROGRESS(1, "审批中"),

    /** 全部审批通过，等执行 */
    APPROVED(2, "已通过"),

    /** 任意一级拒绝 */
    REJECTED(3, "已拒绝"),

    /** 申请人撤回 */
    WITHDRAWN(4, "已撤回"),

    /** 执行节点已完成 */
    COMPLETED(5, "已完成");

    private final Integer code;
    private final String label;

    WorkflowStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public Integer getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static WorkflowStatusEnum of(Integer code) {
        if (code == null) return null;
        for (WorkflowStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
