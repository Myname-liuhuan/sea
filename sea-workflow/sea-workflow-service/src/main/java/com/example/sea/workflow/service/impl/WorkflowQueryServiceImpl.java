package com.example.sea.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.dto.WorkflowDetailDTO;
import com.example.sea.workflow.api.param.WorkflowTaskQueryParam;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import com.example.sea.workflow.converter.WorkflowApprovalConverter;
import com.example.sea.workflow.converter.WorkflowTaskConverter;
import com.example.sea.workflow.dao.WorkflowApprovalMapper;
import com.example.sea.workflow.dao.WorkflowTaskMapper;
import com.example.sea.workflow.entity.WorkflowApprovalPO;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import com.example.sea.workflow.service.IWorkflowQueryService;
import lombok.RequiredArgsConstructor;
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
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());
        long total = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId)).count();

        List<String> taskNos = flowTasks.stream()
                .map(t -> (String) taskService.getVariable(t.getExecutionId(), "taskNo"))
                .filter(s -> s != null)
                .collect(Collectors.toList());

        List<WorkflowTaskPO> tasks = taskNos.isEmpty()
                ? Collections.emptyList()
                : taskMapper.selectBatchIds(taskNos.stream().map(this::findIdByTaskNo).collect(Collectors.toList()));

        List<WorkflowTaskVO> vos = tasks.stream().map(taskConverter::entityToVo).collect(Collectors.toList());
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
    public CommonResult<WorkflowDetailDTO> detail(String taskNo) {
        LambdaQueryWrapper<WorkflowTaskPO> w = Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getTaskNo, taskNo);
        WorkflowTaskPO task = taskMapper.selectOne(w);
        if (task == null) throw new BusinessException("工单不存在");

        Long applicant = SecurityContextUtil.getUserId();
        boolean viewerIsApplicant = applicant != null && applicant.equals(task.getApplicantId());
        boolean viewerIsAdmin = isCurrentUserAdmin();
        if (!viewerIsApplicant && !viewerIsAdmin) {
            // 当前审批人也可读；这里松绑，后续收紧
        }

        List<WorkflowApprovalPO> approvals = approvalMapper.selectList(
                Wrappers.<WorkflowApprovalPO>lambdaQuery()
                        .eq(WorkflowApprovalPO::getTaskId, task.getId())
                        .orderByAsc(WorkflowApprovalPO::getNodeOrder));

        WorkflowDetailDTO dto = new WorkflowDetailDTO();
        dto.setTask(taskConverter.entityToVo(task));
        dto.setApprovals(approvalConverter.entityListToVoList(approvals));
        return CommonResult.success(dto);
    }

    private boolean isCurrentUserAdmin() {
        // TODO M2+E：接入 SecurityContextUtil 的角色解析。暂用空实现
        return false;
    }

    private Long findIdByTaskNo(String taskNo) {
        return taskMapper.selectOne(Wrappers.<WorkflowTaskPO>lambdaQuery()
                .eq(WorkflowTaskPO::getTaskNo, taskNo)).getId();
    }

    private PageResult<WorkflowTaskVO> toPageResult(LambdaQueryWrapper<WorkflowTaskPO> wrapper) {
        Page<WorkflowTaskPO> page = new Page<>(1L, 10L);
        IPage<WorkflowTaskPO> result = taskMapper.selectPage(page, wrapper);
        return new PageResult<>(
                taskConverter.entityListToVoList(result.getRecords()),
                result.getTotal(),
                result.getCurrent(),
                result.getSize());
    }

    @SuppressWarnings("unused")
    private Map<String, Object> safeMap(Task t) {
        return Collections.emptyMap();
    }
}
