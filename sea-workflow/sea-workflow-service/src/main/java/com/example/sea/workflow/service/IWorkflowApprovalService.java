package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.param.ApproveRequest;
import com.example.sea.workflow.api.param.ReassignRequest;

/**
 * 审批人侧：通过 / 拒绝 / 转交 / 委派。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IWorkflowApprovalService {

    /**
     * 通过或拒绝工单当前待办。
     *
     * <p>通过 → Flowable 推进到下一节点（最终进入 execute_reset 服务任务）；
     * 拒绝 → 流程实例走 errorEndEvent，task.status=REJECTED。
     */
    CommonResult<Void> approve(ApproveRequest request);

    /**
     * 审批人把当前待办转交给另一位用户。
     *
     * <p>不推进流程；只改 taskService assignee。原审批人记录一条
     * approved=null(由 comment "转交给 {toUserId}" 标识)、delegated_from=
     * 当前审批人的流水。
     */
    CommonResult<Void> reassign(ReassignRequest request);
}
