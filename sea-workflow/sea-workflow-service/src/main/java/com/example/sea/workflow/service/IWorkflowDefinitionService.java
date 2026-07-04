package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.vo.WorkflowDefinitionVO;

import java.util.List;

/**
 * 流程定义查询（M2 仅做 list，二期再做增删改）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IWorkflowDefinitionService {

    CommonResult<List<WorkflowDefinitionVO>> listByBusinessType(String businessType);
}
