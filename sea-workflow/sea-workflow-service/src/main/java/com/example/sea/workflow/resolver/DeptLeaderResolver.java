package com.example.sea.workflow.resolver;

import com.example.sea.workflow.api.feign.SystemFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * "dept_leader" 用户任务创建前的解析器（ExecutionListener 形式）。
 *
 * <p>把流程变量 deptLeaderId 在任务创建时填上，失败则置空字符串。
 *
 * <p>当前默认路线走 {@code WorkflowApplyServiceImpl} 启动流程时把 deptLeaderId
 * 作为变量一次传过去（避免每个用户任务都做一次 Feign）；本类保留以备
 * 后来按 listener 形式注入。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component("deptLeaderResolver")
@RequiredArgsConstructor
public class DeptLeaderResolver implements ExecutionListener {

    private final SystemFeignClient systemFeignClient;

    @Override
    public void notify(DelegateExecution execution) {
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
}
