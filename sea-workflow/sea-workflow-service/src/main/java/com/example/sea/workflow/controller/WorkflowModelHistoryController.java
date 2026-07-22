package com.example.sea.workflow.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.constants.WorkflowPermissionConstants;
import com.example.sea.workflow.api.vo.WorkflowModelVersionVO;
import com.example.sea.workflow.service.IWorkflowModelHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 流程模型历史版本 REST 接口。
 *
 * <p>路径 {@code /api/workflow/model/history/**}。
 *
 * @author liuhuan
 * @date 2026-07-19
 */
@Tag(name = "工作流-模型历史", description = "流程模型历史版本列表 / 回滚 / diff 取 XML")
@RestController
@RequestMapping("/model/history")
@RequiredArgsConstructor
public class WorkflowModelHistoryController {

    private final IWorkflowModelHistoryService historyService;

    @GetMapping("/list/{modelId}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_READ + "')")
    @Operation(summary = "列出模型的历史版本（不含 bpmn_xml 全文）")
    public CommonResult<List<WorkflowModelVersionVO>> list(@PathVariable String modelId) {
        return historyService.listVersions(modelId);
    }

    @GetMapping("/bpmn/{modelId}/{version}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_READ + "')")
    @Operation(summary = "取出指定历史版本的 BPMN XML（diff / 回滚预览）")
    public CommonResult<String> getBpmn(@PathVariable String modelId,
                                        @PathVariable int version) {
        return historyService.getVersionBpmn(modelId, version);
    }

    @PostMapping("/rollback/{modelId}/{version}")
    @PreAuthorize("hasAuthority('" + WorkflowPermissionConstants.WORKFLOW_MODEL_WRITE + "')")
    @Operation(summary = "回滚到指定历史版本（写入现行模型，并新增一条 history 快照）")
    public CommonResult<Integer> rollback(@PathVariable String modelId,
                                          @PathVariable int version,
                                          @RequestParam(required = false) String comment) {
        return historyService.rollbackToVersion(modelId, version, comment);
    }
}