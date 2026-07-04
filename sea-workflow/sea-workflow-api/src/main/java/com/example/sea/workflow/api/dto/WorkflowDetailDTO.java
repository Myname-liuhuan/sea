package com.example.sea.workflow.api.dto;

import com.example.sea.workflow.api.vo.WorkflowApprovalVO;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 工单详情：任务本体 + 审批链路。
 *
 * <p>详情页审批链路用 Arco a-steps 渲染：本字段 taskOnTop，
 * 链路上 additionalApprovals 由旧到新排序展示。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@Schema(description = "工单详情")
public class WorkflowDetailDTO {

    @Schema(description = "工单本体")
    private WorkflowTaskVO task;

    @Schema(description = "审批链路（按 node_order 升序）")
    private List<WorkflowApprovalVO> approvals;
}
