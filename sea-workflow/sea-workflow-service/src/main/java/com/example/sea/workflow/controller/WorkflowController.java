package com.example.sea.workflow.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.workflow.api.constants.WorkflowPermissionConstants;
import com.example.sea.workflow.api.dto.ApplyResultDTO;
import com.example.sea.workflow.api.dto.WorkflowDetailDTO;
import com.example.sea.workflow.api.param.ApplyRequest;
import com.example.sea.workflow.api.param.ApproveRequest;
import com.example.sea.workflow.api.param.ReassignRequest;
import com.example.sea.workflow.api.param.WorkflowTaskQueryParam;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import com.example.sea.workflow.service.IWorkflowApplyService;
import com.example.sea.workflow.service.IWorkflowApprovalService;
import com.example.sea.workflow.service.IWorkflowQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单主流程接口。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Tag(name = "工作流-工单", description = "重置密码工单的申请 / 审批 / 转交 / 查询")
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final IWorkflowApplyService applyService;
    private final IWorkflowApprovalService approvalService;
    private final IWorkflowQueryService queryService;

    /**
     * 发起重置密码申请。
     */
    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated() and hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_APPLY + "')")
    @Operation(summary = "发起重置密码申请")
    public CommonResult<ApplyResultDTO> apply(@RequestBody @Valid ApplyRequest request,
                                              @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return applyService.apply(request, idempotencyKey);
    }

    /**
     * 审批通过 / 拒绝。
     */
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_APPROVE + "')")
    @Operation(summary = "审批通过 / 拒绝")
    public CommonResult<Void> approve(@RequestBody @Valid ApproveRequest request) {
        return approvalService.approve(request);
    }

    /**
     * 审批人转交 / 委派。
     */
    @PostMapping("/reassign")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_APPROVE + "')")
    @Operation(summary = "审批人转交 / 委派")
    public CommonResult<Void> reassign(@RequestBody @Valid ReassignRequest request) {
        return approvalService.reassign(request);
    }

    /**
     * 我的申请。
     */
    @GetMapping("/my-applications")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "我提交的工单")
    public CommonResult<PageResult<WorkflowTaskVO>> myApplications(WorkflowTaskQueryParam query) {
        return queryService.myApplications(query);
    }

    /**
     * 待我审批。
     */
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_APPROVE + "')")
    @Operation(summary = "待我审批的工单")
    public CommonResult<PageResult<WorkflowTaskVO>> pendingApprovals(WorkflowTaskQueryParam query) {
        return queryService.pendingApprovals(query);
    }

    /**
     * 工单详情。
     */
    @GetMapping("/detail/{taskNo}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "工单详情")
    public CommonResult<WorkflowDetailDTO> detail(@PathVariable String taskNo) {
        return queryService.detail(taskNo);
    }
}
