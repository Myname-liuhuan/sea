package com.example.sea.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.param.ApproveRequest;
import com.example.sea.workflow.api.param.ReassignRequest;
import com.example.sea.workflow.constants.WorkflowStatusEnum;
import com.example.sea.workflow.dao.WorkflowApprovalMapper;
import com.example.sea.workflow.dao.WorkflowTaskMapper;
import com.example.sea.workflow.entity.WorkflowApprovalPO;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import com.example.sea.workflow.service.IWorkflowApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批 / 转交实现。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowApprovalServiceImpl implements IWorkflowApprovalService {

    private final WorkflowTaskMapper taskMapper;
    private final WorkflowApprovalMapper approvalMapper;
    private final TaskService taskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> approve(ApproveRequest request) {
        Long approverId = SecurityContextUtil.getUserId();
        if (approverId == null) {
            return CommonResult.failed("未登录");
        }

        WorkflowTaskPO task = loadInProgressTask(request.getTaskNo());

        Task flowTask = findActiveFlowTask(task.getFlowInstanceId());
        if (flowTask == null) {
            return CommonResult.failed("当前无待办");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", Boolean.TRUE.equals(request.getApproved()));
        variables.put("comment", request.getComment());

        try {
            taskService.complete(flowTask.getId(), variables);
        } catch (Exception e) {
            log.warn("workflow.approve taskService.complete failed taskNo={}", request.getTaskNo(), e);
            throw new BusinessException("审批提交失败: " + e.getMessage());
        }

        WorkflowApprovalPO approval = new WorkflowApprovalPO();
        approval.setTaskId(task.getId());
        approval.setNodeKey(flowTask.getTaskDefinitionKey());
        approval.setNodeOrder(currentNodeOrder(task.getId()));
        approval.setApproverId(approverId);
        approval.setApproved(Boolean.TRUE.equals(request.getApproved()) ? 1 : 0);
        approval.setComment(request.getComment());
        approvalMapper.insert(approval);

        if (!Boolean.TRUE.equals(request.getApproved())) {
            updateTaskStatus(task, WorkflowStatusEnum.REJECTED);
        }
        return CommonResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> reassign(ReassignRequest request) {
        Long approverId = SecurityContextUtil.getUserId();
        if (approverId == null) {
            return CommonResult.failed("未登录");
        }
        WorkflowTaskPO task = loadInProgressTask(request.getTaskNo());
        Task flowTask = findActiveFlowTask(task.getFlowInstanceId());
        if (flowTask == null) {
            return CommonResult.failed("当前无待办");
        }

        taskService.setAssignee(flowTask.getId(), String.valueOf(request.getToUserId()));

        WorkflowApprovalPO approval = new WorkflowApprovalPO();
        approval.setTaskId(task.getId());
        approval.setNodeKey(flowTask.getTaskDefinitionKey());
        approval.setNodeOrder(currentNodeOrder(task.getId()));
        approval.setApproverId(approverId);
        approval.setApproved(-1);
        approval.setComment("转交给 userId=" + request.getToUserId()
                + (request.getComment() == null ? "" : "，" + request.getComment()));
        approval.setDelegatedFrom(approverId);
        approvalMapper.insert(approval);
        return CommonResult.success();
    }

    private WorkflowTaskPO loadInProgressTask(String taskNo) {
        LambdaQueryWrapper<WorkflowTaskPO> wrapper = Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getTaskNo, taskNo);
        WorkflowTaskPO task = taskMapper.selectOne(wrapper);
        if (task == null) {
            throw new BusinessException("工单不存在");
        }
        if (task.getStatus() == null
                || task.getStatus() < WorkflowStatusEnum.IN_PROGRESS.getCode()
                || task.getStatus() == WorkflowStatusEnum.REJECTED.getCode()
                || task.getStatus() == WorkflowStatusEnum.WITHDRAWN.getCode()
                || task.getStatus() == WorkflowStatusEnum.COMPLETED.getCode()) {
            throw new BusinessException("工单不在可审批状态");
        }
        return task;
    }

    private Task findActiveFlowTask(String flowInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(flowInstanceId)
                .list();
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    private int currentNodeOrder(Long taskId) {
        Long count = approvalMapper.selectCount(Wrappers.<WorkflowApprovalPO>lambdaQuery()
                .eq(WorkflowApprovalPO::getTaskId, taskId));
        return count.intValue() + 1;
    }

    private void updateTaskStatus(WorkflowTaskPO task, WorkflowStatusEnum target) {
        task.setStatus(target.getCode());
        taskMapper.updateById(task);
    }
}
