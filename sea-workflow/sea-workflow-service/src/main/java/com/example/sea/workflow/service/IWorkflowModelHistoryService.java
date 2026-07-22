package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.vo.WorkflowModelVersionVO;

import java.util.List;

/**
 * 流程模型历史版本服务。
 *
 * @author liuhuan
 * @date 2026-07-19
 */
public interface IWorkflowModelHistoryService {

    /**
     * 保存一份历史快照（在 saveBpmnXml 之后调用）。
     *
     * <p>自动计算下一个 version（max+1），无需调用方传入。
     *
     * @param modelId        ACT_RE_MODEL.ID_
     * @param bpmnXml        当前 BPMN
     * @param svg            可选 SVG
     * @param changeComment  保存说明（可选）
     * @return 新历史的 version
     */
    int recordHistory(String modelId, String bpmnXml, String svg, String changeComment);

    /**
     * 列模型的所有历史版本（按 version 倒序）。
     */
    CommonResult<List<WorkflowModelVersionVO>> listVersions(String modelId);

    /**
     * 取指定版本号的 BPMN 全文（用于 diff 与回滚预览）。
     */
    CommonResult<String> getVersionBpmn(String modelId, int version);

    /**
     * 回滚到指定版本：把该版本的 bpmn_xml 写回 ACT_RE_MODEL.EDITOR_SOURCE_，
     * 并新增一条 history 记录（版本号继续递增）。
     *
     * @param modelId    ACT_RE_MODEL.ID_
     * @param version    要回滚到的历史版本号
     * @param comment    回滚说明（写入新历史）
     * @return 新历史的 version
     */
    CommonResult<Integer> rollbackToVersion(String modelId, int version, String comment);
}