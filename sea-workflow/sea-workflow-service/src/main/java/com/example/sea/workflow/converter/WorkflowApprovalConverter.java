package com.example.sea.workflow.converter;

import com.example.sea.workflow.api.vo.WorkflowApprovalVO;
import com.example.sea.workflow.entity.WorkflowApprovalPO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 审批记录 PO ↔ VO。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper(componentModel = "spring")
public interface WorkflowApprovalConverter {

    WorkflowApprovalVO entityToVo(WorkflowApprovalPO po);

    List<WorkflowApprovalVO> entityListToVoList(List<WorkflowApprovalPO> list);
}
