package com.example.sea.workflow.converter;

import com.example.sea.workflow.api.vo.ApplyResultVO;
import com.example.sea.workflow.api.dto.ApplyRequest;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import com.example.sea.workflow.constants.WorkflowStatusEnum;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 工单 PO ↔ VO 转换。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper(componentModel = "spring")
public interface WorkflowTaskConverter {

    /** 申请入参 → PO（运行时由 service 填充 applicantId/targetUserId/status/version 等） */
    @Mapping(target = "applicantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskNo", ignore = true)
    @Mapping(target = "flowInstanceId", ignore = true)
    @Mapping(target = "currentNode", ignore = true)
    @Mapping(target = "temporaryPasswordCipher", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "businessType", ignore = true)
    @Mapping(target = "businessKey", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    WorkflowTaskPO applyToEntity(ApplyRequest request);

    /** PO → VO。状态码转状态文本。 */
    @Mapping(target = "statusLabel", expression = "java(com.example.sea.workflow.constants.WorkflowStatusEnum.of(po.getStatus()) == null ? null : com.example.sea.workflow.constants.WorkflowStatusEnum.of(po.getStatus()).getLabel())")
    WorkflowTaskVO entityToVo(WorkflowTaskPO po);

    List<WorkflowTaskVO> entityListToVoList(List<WorkflowTaskPO> list);

    ApplyResultVO toApplyResult(WorkflowTaskPO po);
}
