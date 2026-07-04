package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.dto.ApplyResultDTO;
import com.example.sea.workflow.api.param.ApplyRequest;

/**
 * 申请人侧：发起重置密码工单。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IWorkflowApplyService {

    /**
     * 提交申请。
     *
     * <p>流程：
     * <ol>
     *   <li>校验目标用户存在并取能级</li>
     *   <li>写 workflow_task（PENDING）</li>
     *   <li>启动 Flowable 实例 (reset_password)，传上下文变量</li>
     *   <li>更新 task.flowInstanceId, status=IN_PROGRESS</li>
     * </ol>
     *
     * @param idempotencyKey 可空；提供则 10 分钟内同 key 不重复创建
     */
    CommonResult<ApplyResultDTO> apply(ApplyRequest request, String idempotencyKey);
}
