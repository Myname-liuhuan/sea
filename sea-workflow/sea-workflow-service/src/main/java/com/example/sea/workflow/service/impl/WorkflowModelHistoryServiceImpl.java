package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.vo.WorkflowModelVersionVO;
import com.example.sea.workflow.dao.WorkflowModelHistoryMapper;
import com.example.sea.workflow.entity.WorkflowModelHistoryPO;
import com.example.sea.workflow.service.IWorkflowModelHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程模型历史版本服务实现。
 *
 * @author liuhuan
 * @date 2026-07-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowModelHistoryServiceImpl implements IWorkflowModelHistoryService {

    private final WorkflowModelHistoryMapper historyMapper;
    private final RepositoryService repositoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recordHistory(String modelId, String bpmnXml, String svg, String changeComment) {
        if (modelId == null || bpmnXml == null) {
            throw new BusinessException("modelId / bpmnXml 不能为空");
        }
        int nextVersion = historyMapper.maxVersion(modelId) + 1;

        WorkflowModelHistoryPO row = new WorkflowModelHistoryPO()
                .setModelId(modelId)
                .setVersion(nextVersion)
                .setBpmnXml(bpmnXml)
                .setSvg(svg)
                .setChangeComment(changeComment)
                .setCreatorId(SecurityContextUtil.getUserId())
                .setCreatorName(SecurityContextUtil.getUsername())
                .setCreateTime(LocalDateTime.now());

        historyMapper.insert(row);
        log.info("workflow.history.recorded modelId={} version={} by={}",
                modelId, nextVersion, SecurityContextUtil.getUsername());
        return nextVersion;
    }

    @Override
    public CommonResult<List<WorkflowModelVersionVO>> listVersions(String modelId) {
        // 先校验模型存在
        if (repositoryService.createModelQuery().modelId(modelId).count() == 0) {
            return CommonResult.failed("模型不存在: " + modelId);
        }
        List<WorkflowModelHistoryPO> rows = historyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkflowModelHistoryPO>()
                        .eq(WorkflowModelHistoryPO::getModelId, modelId)
                        .orderByDesc(WorkflowModelHistoryPO::getVersion));

        Integer latest = rows.isEmpty() ? null : rows.get(0).getVersion();
        List<WorkflowModelVersionVO> result = new ArrayList<>(rows.size());
        for (WorkflowModelHistoryPO row : rows) {
            WorkflowModelVersionVO vo = new WorkflowModelVersionVO();
            vo.setId(row.getId());
            vo.setModelId(row.getModelId());
            vo.setVersion(row.getVersion());
            vo.setChangeComment(row.getChangeComment());
            vo.setCreatorId(row.getCreatorId());
            vo.setCreatorName(row.getCreatorName());
            vo.setCreateTime(row.getCreateTime());
            vo.setLatest(latest != null && latest.equals(row.getVersion()));
            result.add(vo);
        }
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<String> getVersionBpmn(String modelId, int version) {
        WorkflowModelHistoryPO row = historyMapper.selectByVersion(modelId, version);
        if (row == null) {
            return CommonResult.failed("历史版本不存在: modelId=" + modelId + " v=" + version);
        }
        return CommonResult.success(row.getBpmnXml());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Integer> rollbackToVersion(String modelId, int version, String comment) {
        Model m = repositoryService.createModelQuery().modelId(modelId).singleResult();
        if (m == null) {
            return CommonResult.failed("模型不存在: " + modelId);
        }
        WorkflowModelHistoryPO row = historyMapper.selectByVersion(modelId, version);
        if (row == null) {
            return CommonResult.failed("历史版本不存在: v=" + version);
        }

        // 1. 把目标版本 XML 写回 ACT_RE_MODEL.EDITOR_SOURCE_
        repositoryService.addModelEditorSource(modelId,
                row.getBpmnXml().getBytes(StandardCharsets.UTF_8));

        // 2. 写一条新的历史快照（version 自增 +1），保留回滚操作痕迹
        int newVersion = recordHistory(modelId, row.getBpmnXml(),
                row.getSvg(),
                comment != null ? comment : "rollback to v" + version);

        log.info("workflow.model.rollback modelId={} from=v? to=v{} new=v{} by={}",
                modelId, version, newVersion, SecurityContextUtil.getUsername());

        return CommonResult.success(newVersion);
    }
}