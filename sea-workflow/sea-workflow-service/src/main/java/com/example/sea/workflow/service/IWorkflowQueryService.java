package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.workflow.api.dto.WorkflowDetailDTO;
import com.example.sea.workflow.api.param.WorkflowTaskQueryParam;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;

/**
 * 工单查询。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IWorkflowQueryService {

    /** 当前登录用户提交的所有工单 */
    CommonResult<PageResult<WorkflowTaskVO>> myApplications(WorkflowTaskQueryParam query);

    /** 当前登录用户作为审批人需要处理的工单 */
    CommonResult<PageResult<WorkflowTaskVO>> pendingApprovals(WorkflowTaskQueryParam query);

    /** 全部工单（监控页，ADMIN） */
    CommonResult<PageResult<WorkflowTaskVO>> allTasks(WorkflowTaskQueryParam query);

    /** 工单详情：task + 审批链路 */
    CommonResult<WorkflowDetailDTO> detail(String taskNo);
}
