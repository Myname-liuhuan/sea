package com.example.sea.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.vo.WorkflowDetailVO;
import com.example.sea.workflow.api.param.WorkflowTaskQueryParam;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import com.example.sea.workflow.api.vo.WorkflowApprovalVO;
import com.example.sea.workflow.converter.WorkflowApprovalConverter;
import com.example.sea.workflow.converter.WorkflowTaskConverter;
import com.example.sea.workflow.dao.WorkflowApprovalMapper;
import com.example.sea.workflow.dao.WorkflowTaskMapper;
import com.example.sea.workflow.entity.WorkflowApprovalPO;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import com.example.sea.workflow.service.IWorkflowQueryService;
import com.example.sea.workflow.service.WorkflowNameEnricher;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询实现。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Service
@RequiredArgsConstructor
public class WorkflowQueryServiceImpl implements IWorkflowQueryService {

    private final WorkflowTaskMapper taskMapper;
    private final WorkflowApprovalMapper approvalMapper;
    private final WorkflowTaskConverter taskConverter;
    private final WorkflowApprovalConverter approvalConverter;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final WorkflowNameEnricher nameEnricher;

    @Override
    public CommonResult<PageResult<WorkflowTaskVO>> myApplications(WorkflowTaskQueryParam query) {
        Long applicantId = SecurityContextUtil.getUserId();
        if (applicantId == null) return CommonResult.failed("未登录");

        LambdaQueryWrapper<WorkflowTaskPO> wrapper = Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getApplicantId, applicantId)
                .eq(query.getStatus() != null, WorkflowTaskPO::getStatus, query.getStatus())
                .eq(query.getUrgency() != null, WorkflowTaskPO::getUrgency, query.getUrgency())
                .orderByDesc(WorkflowTaskPO::getCreateTime);

        return CommonResult.success(toPageResult(wrapper));
    }

    @Override
    public CommonResult<PageResult<WorkflowTaskVO>> pendingApprovals(WorkflowTaskQueryParam query) {
        Long userId = SecurityContextUtil.getUserId();
        if (userId == null) return CommonResult.failed("未登录");

        List<Task> flowTasks = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId))
                .listPage((int) ((query.getPageNum() - 1) * query.getPageSize()),
                        (int) (long) query.getPageSize());
        long total = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId)).count();

        List<String> taskNos = flowTasks.stream()
                // taskService.getVariable 在 Flowable 里实际执行 GetTaskVariableCmd，期望的是 taskId，
                // 不是 executionId；executionId 走 runtimeService.getVariable。这里挂的是 taskNo 流程变量。
                .map(t -> (String) taskService.getVariable(t.getId(), "taskNo"))
                .filter(s -> s != null)
                .collect(Collectors.toList());

        List<WorkflowTaskPO> tasks = taskNos.isEmpty()
                ? Collections.emptyList()
                : taskMapper.selectBatchIds(taskNos.stream().map(this::findIdByTaskNo).collect(Collectors.toList()));

        List<WorkflowTaskVO> vos = tasks.stream().map(taskConverter::entityToVo).collect(Collectors.toList());
        nameEnricher.enrichTaskNames(vos);
        return CommonResult.success(new PageResult<>(vos, total, query.getPageNum(), query.getPageSize()));
    }

    @Override
    public CommonResult<PageResult<WorkflowTaskVO>> allTasks(WorkflowTaskQueryParam query) {
        LambdaQueryWrapper<WorkflowTaskPO> wrapper = Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(query.getStatus() != null, WorkflowTaskPO::getStatus, query.getStatus())
                .eq(query.getUrgency() != null, WorkflowTaskPO::getUrgency, query.getUrgency())
                .eq(query.getApplicantId() != null, WorkflowTaskPO::getApplicantId, query.getApplicantId())
                .eq(query.getTargetUserId() != null, WorkflowTaskPO::getTargetUserId, query.getTargetUserId())
                .orderByDesc(WorkflowTaskPO::getCreateTime);

        return CommonResult.success(toPageResult(wrapper));
    }

    @Override
    public CommonResult<WorkflowDetailVO> detail(String taskNo) {
        LambdaQueryWrapper<WorkflowTaskPO> w = Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getTaskNo, taskNo);
        WorkflowTaskPO task = taskMapper.selectOne(w);
        if (task == null) throw new BusinessException("工单不存在");

        // 访问控制：申请人 / 当前审批人 / 超管 三选一
        Long viewerId = SecurityContextUtil.getUserId();
        boolean viewerIsApplicant = viewerId != null && viewerId.equals(task.getApplicantId());
        boolean viewerIsCurrentApprover = viewerId != null && isCurrentApprover(taskNo, String.valueOf(viewerId));
        boolean viewerIsAdmin = SecurityContextUtil.hasAuthority("*:*:*");
        if (!viewerIsApplicant && !viewerIsCurrentApprover && !viewerIsAdmin) {
            throw new BusinessException("无权查看该工单");
        }

        List<WorkflowApprovalPO> approvals = approvalMapper.selectList(
                Wrappers.<WorkflowApprovalPO>lambdaQuery()
                        .eq(WorkflowApprovalPO::getTaskId, task.getId())
                        .orderByAsc(WorkflowApprovalPO::getNodeOrder));

        WorkflowDetailVO dto = new WorkflowDetailVO();
        WorkflowTaskVO taskVo = taskConverter.entityToVo(task);
        List<WorkflowApprovalVO> approvalVos = approvalConverter.entityListToVoList(approvals);
        nameEnricher.enrichTaskNames(List.of(taskVo));
        nameEnricher.enrichApprovalNames(approvalVos);
        dto.setTask(taskVo);
        dto.setApprovals(approvalVos);
        return CommonResult.success(dto);
    }

    /**
     * 当前用户是否是该工单的当前/历史审批人。
     * <p>当前：Flowable TaskService 查 ACT_RU_TASK；
     * 历史：Flowable HistoryService 查 ACT_HI_TASKINST，
     * 用于审批通过后 admin 仍能查看工单详情。
     */
    private boolean isCurrentApprover(String taskNo, String userId) {
        try {
            long active = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .processVariableValueEquals("taskNo", taskNo)
                    .count();
            if (active > 0) return true;
            long historical = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userId)
                    .processVariableValueEquals("taskNo", taskNo)
                    .count();
            return historical > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private Long findIdByTaskNo(String taskNo) {
        return taskMapper.selectOne(Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getTaskNo, taskNo)).getId();
    }

    private PageResult<WorkflowTaskVO> toPageResult(LambdaQueryWrapper<WorkflowTaskPO> wrapper) {
        Page<WorkflowTaskPO> page = new Page<>(1L, 10L);
        IPage<WorkflowTaskPO> result = taskMapper.selectPage(page, wrapper);
        List<WorkflowTaskVO> vos = taskConverter.entityListToVoList(result.getRecords());
        nameEnricher.enrichTaskNames(vos);
        return new PageResult<>(vos, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @SuppressWarnings("unused")
    private Map<String, Object> safeMap(Task t) {
        return Collections.emptyMap();
    }
}
