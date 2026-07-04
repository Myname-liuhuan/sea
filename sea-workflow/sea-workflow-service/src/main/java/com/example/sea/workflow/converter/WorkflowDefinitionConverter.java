package com.example.sea.workflow.converter;

import com.example.sea.workflow.api.vo.WorkflowDefinitionVO;
import com.example.sea.workflow.entity.WorkflowDefinitionPO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 流程定义 PO ↔ VO。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper(componentModel = "spring")
public interface WorkflowDefinitionConverter {

    WorkflowDefinitionVO entityToVo(WorkflowDefinitionPO po);

    List<WorkflowDefinitionVO> entityListToVoList(List<WorkflowDefinitionPO> list);
}
