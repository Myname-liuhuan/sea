package com.example.sea.workflow.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.workflow.api.constants.WorkflowPermissionConstants;
import com.example.sea.workflow.api.param.WorkflowTaskQueryParam;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import com.example.sea.workflow.service.IWorkflowQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单监控（仅管理员）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Tag(name = "工作流-监控", description = "管理员查看所有工单")
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class WorkflowMonitorController {

    private final IWorkflowQueryService queryService;

    @GetMapping("/all-tasks")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MONITOR + "')")
    @Operation(summary = "工单监控列表")
    public CommonResult<PageResult<WorkflowTaskVO>> allTasks(WorkflowTaskQueryParam query) {
        return queryService.allTasks(query);
    }
}
