package com.example.sea.workflow.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.param.AdminEmergencyRequest;
import com.example.sea.workflow.api.vo.WorkflowDefinitionVO;
import com.example.sea.workflow.service.IWorkflowAdminService;
import com.example.sea.workflow.service.IWorkflowDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员通道：紧急重置 + 流程定义。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Tag(name = "工作流-管理", description = "管理员紧急通道与流程定义查询")
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowAdminController {

    private final IWorkflowAdminService adminService;
    private final IWorkflowDefinitionService definitionService;

    @PostMapping("/admin-emergency-reset")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理员紧急通道重置密码（绕过审批）")
    public CommonResult<Void> emergencyReset(@RequestBody @Valid AdminEmergencyRequest request) {
        return adminService.emergencyReset(request);
    }

    @PostMapping("/definition/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "流程定义查询（按业务类型）")
    public CommonResult<List<WorkflowDefinitionVO>> listDefinitions(@RequestBody String businessType) {
        return definitionService.listByBusinessType(businessType);
    }
}
