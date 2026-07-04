package com.example.sea.workflow.constants;

/**
 * 流程节点标识常量，对应 BPMN 中 userTask 与 serviceTask 的 id。
 *
 * <p>审批记录 workflow_approval.node_key 引用此处常量，便于跨代码定位。
 */
public final class WorkflowNodeKeyConstants {

    private WorkflowNodeKeyConstants() {}

    /** 部门主管审批节点 */
    public static final String DEPT_LEADER = "dept_leader";

    /** HR 复审节点 */
    public static final String HR_REVIEW = "hr_review";

    /** 执行密码重置服务任务 */
    public static final String EXECUTE_RESET = "execute_reset";
}
