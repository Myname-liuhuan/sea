package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.workflow.api.dto.CreateModelRequest;
import com.example.sea.workflow.api.dto.SaveBpmnRequest;
import com.example.sea.workflow.api.dto.UpdateModelRequest;
import com.example.sea.workflow.api.param.WorkflowModelQueryParam;
import com.example.sea.workflow.api.vo.DeployModelResultVO;
import com.example.sea.workflow.api.vo.WorkflowModelListItemVO;
import com.example.sea.workflow.api.vo.WorkflowModelVO;

/**
 * 流程设计器服务：管理 Flowable ACT_RE_MODEL 元数据 + BPMN XML + 部署。
 *
 * <p>所有方法都直接走 {@link org.flowable.engine.RepositoryService}，
 * 不维护业务侧 Model 表（业务侧字段塞进 {@code ACT_RE_MODEL.META_INFO_}）。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
public interface IWorkflowModelerService {

    /** 列表分页查询 */
    CommonResult<PageResult<WorkflowModelListItemVO>> listModels(WorkflowModelQueryParam query);

    /** 单条详情 */
    CommonResult<WorkflowModelVO> getModel(String id);

    /** 新建模型：写 ACT_RE_MODEL + 初始化空 BPMN XML */
    CommonResult<WorkflowModelVO> createModel(CreateModelRequest req);

    /** 更新模型元数据（不含 BPMN 内容） */
    CommonResult<WorkflowModelVO> updateModel(String id, UpdateModelRequest req);

    /** 删除模型 */
    CommonResult<Void> deleteModel(String id);

    /** 取出 BPMN XML 文本 */
    CommonResult<String> getBpmnXml(String id);

    /** 保存 BPMN XML（先做 camunda→flowable 命名空间净化，再走引擎解析校验） */
    CommonResult<Void> saveBpmnXml(String id, SaveBpmnRequest req);

    /** 部署模型：解析已保存的 XML，createDeployment → deploy，回填 deploymentId */
    CommonResult<DeployModelResultVO> deployModel(String id);

    /** 基于现有模型克隆出一个新模型（XML 一并复制） */
    CommonResult<WorkflowModelVO> cloneModel(String id, String newName);
}