package com.example.sea.workflow.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.workflow.api.constants.WorkflowPermissionConstants;
import com.example.sea.workflow.api.dto.CreateModelRequest;
import com.example.sea.workflow.api.dto.SaveBpmnRequest;
import com.example.sea.workflow.api.dto.UpdateModelRequest;
import com.example.sea.workflow.api.param.WorkflowModelQueryParam;
import com.example.sea.workflow.api.vo.DeployModelResultVO;
import com.example.sea.workflow.api.vo.WorkflowModelListItemVO;
import com.example.sea.workflow.api.vo.WorkflowModelVO;
import com.example.sea.workflow.service.IWorkflowModelerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程设计器接口（基于 Flowable RepositoryService）。
 *
 * <p>路径 {@code /api/workflow/model/**}；与 {@link WorkflowController} 共用
 * {@code /api/workflow} 前缀但子路径独立。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Tag(name = "工作流-模型设计器", description = "BPMN 模型的 CRUD + 保存 BPMN + 部署")
@RestController
@RequestMapping("/api/workflow/model")
@RequiredArgsConstructor
public class WorkflowModelerController {

    private final IWorkflowModelerService modelerService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_READ + "')")
    @Operation(summary = "流程模型列表（分页）")
    public CommonResult<PageResult<WorkflowModelListItemVO>> list(WorkflowModelQueryParam query) {
        return modelerService.listModels(query);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_READ + "')")
    @Operation(summary = "模型详情")
    public CommonResult<WorkflowModelVO> get(@PathVariable String id) {
        return modelerService.getModel(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_WRITE + "')")
    @Operation(summary = "新建流程模型")
    public CommonResult<WorkflowModelVO> create(@RequestBody @Valid CreateModelRequest req) {
        return modelerService.createModel(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_WRITE + "')")
    @Operation(summary = "更新流程模型元数据（不含 BPMN XML）")
    public CommonResult<WorkflowModelVO> update(@PathVariable String id,
                                                @RequestBody @Valid UpdateModelRequest req) {
        return modelerService.updateModel(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_DELETE
            + "') or hasRole('ADMIN')")
    @Operation(summary = "删除流程模型")
    public CommonResult<Void> delete(@PathVariable String id) {
        return modelerService.deleteModel(id);
    }

    @GetMapping("/{id}/bpmn")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_READ + "')")
    @Operation(summary = "取出 BPMN XML")
    public CommonResult<String> getBpmn(@PathVariable String id) {
        return modelerService.getBpmnXml(id);
    }

    @PutMapping("/{id}/bpmn")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_WRITE + "')")
    @Operation(summary = "保存 BPMN XML（设计器保存按钮）")
    public CommonResult<Void> saveBpmn(@PathVariable String id,
                                       @RequestBody @Valid SaveBpmnRequest req) {
        return modelerService.saveBpmnXml(id, req);
    }

    @PostMapping("/{id}/deploy")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_DEPLOY
            + "') or hasRole('ADMIN')")
    @Operation(summary = "部署模型为流程定义")
    public CommonResult<DeployModelResultVO> deploy(@PathVariable String id) {
        return modelerService.deployModel(id);
    }

    @PostMapping("/{id}/clone")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_WRITE + "')")
    @Operation(summary = "克隆模型")
    public CommonResult<WorkflowModelVO> clone(@PathVariable String id,
                                               @RequestParam String newName) {
        return modelerService.cloneModel(id, newName);
    }
}