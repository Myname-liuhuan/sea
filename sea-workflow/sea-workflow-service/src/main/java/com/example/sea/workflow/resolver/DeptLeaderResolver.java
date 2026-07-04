package com.example.sea.workflow.resolver;

import com.example.sea.workflow.api.feign.SystemFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * "dept_leader" 用户任务创建前的解析器。
 *
 * <p>把流程变量 deptLeaderId 在任务创建时填上，如果失败则置空字符串
 * 让 BPMN 引擎以 candidate groups 兜底。
 *
 * <p>当前未在 bpmn 中通过 {@code flowable:executionListener} 显式绑定，
 * 实际路线走 {@code WorkflowApplyServiceImpl} 启动流程时把 deptLeaderId
 * 作为变量一次传过去（避免每个用户任务都做一次额外 Feign）。本类作为可
 * 选的 TaskListener 实现保留，便于今后改成 listener 形式。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component("deptLeaderResolver")
@RequiredArgsConstructor
public class DeptLeaderResolver implements TaskListener, ExecutionListener {

    private final SystemFeignClient systemFeignClient;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        Object targetUserId = execution.getVariable("targetUserId");
        if (targetUserId == null) {
            log.warn("deptLeaderResolver.skip reason=missing-targetUserId instance={}",
                    execution.getProcessInstanceId());
            return;
        }
        var resp = systemFeignClient.getUserLeaderId(((Number) targetUserId).longValue());
        if (resp != null && resp.isSuccess() && resp.getData() != null) {
            execution.setVariable("deptLeaderId", String.valueOf(resp.getData()));
        } else {
            execution.setVariable("deptLeaderId", "");
        }
    }

    @Override
    public void onExecutionStart(DelegateExecution execution) {
        // 不在执行开始时处理；保留 hook 位
    }
}
