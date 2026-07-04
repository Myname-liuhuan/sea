package com.example.sea.workflow.constants;

/**
 * 工单紧急程度枚举。
 *
 * <p>对应 workflow_task.urgency 字段。本期紧急程度不影响审批路径
 * （BPMN 走固定分支），保留字段供二期 SLA / 路由升级使用。
 */
public enum WorkflowUrgencyEnum {

    NORMAL(1, "普通"),
    URGENT(2, "紧急");

    private final Integer code;
    private final String label;

    WorkflowUrgencyEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public Integer getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
