package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.dto.CreateModelRequest;
import com.example.sea.workflow.api.dto.SaveBpmnRequest;
import com.example.sea.workflow.api.dto.UpdateModelRequest;
import com.example.sea.workflow.api.param.WorkflowModelQueryParam;
import com.example.sea.workflow.api.vo.DeployModelResultVO;
import com.example.sea.workflow.api.vo.WorkflowModelListItemVO;
import com.example.sea.workflow.api.vo.WorkflowModelVO;
import com.example.sea.workflow.service.IWorkflowModelerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程设计器实现。直接走 Flowable {@link RepositoryService}，不维护业务侧表。
 *
 * <p>业务侧字段（businessType / description / creatorId / creatorName）塞进
 * {@code ACT_RE_MODEL.META_INFO_} 这个 free-form JSON 列。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowModelerServiceImpl implements IWorkflowModelerService {

    /** META_INFO_ schema 版本号（解析时用于兼容性判断）。 */
    private static final String META_VERSION = "1";
    /** 部署资源名约定，便于 ACT_RE_PROCDEF 关联。 */
    private static final String DEPLOYMENT_RESOURCE_NAME = "model.bpmn20.xml";

    /** 空模型模板：含一个 StartEvent 的最小合法 BPMN。 */
    private static final String EMPTY_BPMN_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\""
            + "                  xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\""
            + "                  xmlns:flowable=\"http://flowable.org/bpmn\""
            + "                  xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\""
            + "                  xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\""
            + "                  id=\"definitions\" targetNamespace=\"http://flowable.org/bpmn\">\n"
            + "  <bpmn:process id=\"process_1\" isExecutable=\"true\">\n"
            + "    <bpmn:startEvent id=\"startEvent1\"/>\n"
            + "  </bpmn:process>\n"
            + "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n"
            + "    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"process_1\">\n"
            + "      <bpmndi:BPMNShape id=\"_BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">\n"
            + "        <dc:Bounds x=\"180\" y=\"160\" width=\"36\" height=\"36\"/>\n"
            + "      </bpmndi:BPMNShape>\n"
            + "    </bpmndi:BPMNPlane>\n"
            + "  </bpmndi:BPMNDiagram>\n"
            + "</bpmn:definitions>\n";

    private final RepositoryService repositoryService;
    private final ObjectMapper objectMapper;

    // ===================== 查询 =====================

    @Override
    public CommonResult<PageResult<WorkflowModelListItemVO>> listModels(WorkflowModelQueryParam query) {
        long pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1L : query.getPageNum();
        long pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10L : query.getPageSize();
        String wantedBizType = (query.getBusinessType() == null || query.getBusinessType().isBlank())
                ? null
                : query.getBusinessType().trim();

        // 构建 Flowable 原生查询（name / key / category 由 Flowable 下推到 SQL）
        ModelQuery q = repositoryService.createModelQuery();
        if (query.getName() != null && !query.getName().isBlank()) {
            q.modelNameLike("%" + query.getName().trim() + "%");
        }
        if (query.getKey() != null && !query.getKey().isBlank()) {
            q.modelKey(query.getKey().trim());
        }
        if (query.getCategory() != null && !query.getCategory().isBlank()) {
            q.modelCategory(query.getCategory().trim());
        }
        q.orderByLastUpdateTime().desc();

        List<WorkflowModelListItemVO> pageRows;
        long total;

        if (wantedBizType == null) {
            // 无 businessType 过滤：走 Flowable 原生分页
            total = q.count();
            if (total == 0) {
                pageRows = Collections.emptyList();
            } else {
                List<Model> rows = q.listPage((int) ((pageNum - 1) * pageSize), (int) pageSize);
                pageRows = rows.stream().map(this::toListItemVo).collect(Collectors.toList());
            }
        } else {
            // businessType 存在 META_INFO_ 里，Flowable 无原生过滤；
            // 必须先全量拉 + 内存过滤 + 重新分页（修复 #2/#3：先过滤后分页，total 取过滤后总数）
            List<Model> all = q.list();
            pageRows = all.stream()
                    .map(this::toListItemVo)
                    .filter(v -> wantedBizType.equalsIgnoreCase(v.getBusinessType()))
                    .collect(Collectors.toList());
            total = pageRows.size();
            int from = (int) Math.min((pageNum - 1) * pageSize, pageRows.size());
            int to = (int) Math.min(from + pageSize, pageRows.size());
            pageRows = pageRows.subList(from, to);
        }

        return CommonResult.success(PageResult.of(pageRows, total, pageNum, pageSize));
    }

    @Override
    public CommonResult<WorkflowModelVO> getModel(String id) {
        Model m = repositoryService.createModelQuery().modelId(id).singleResult();
        if (m == null) {
            return CommonResult.failed("模型不存在: " + id);
        }
        return CommonResult.success(toVo(m));
    }

    // ===================== 新建 / 更新 / 删除 =====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<WorkflowModelVO> createModel(CreateModelRequest req) {
        long existed = repositoryService.createModelQuery().modelKey(req.getKey()).count();
        if (existed > 0) {
            return CommonResult.failed("模型 key 已存在: " + req.getKey());
        }

        Model model = repositoryService.newModel();
        model.setKey(req.getKey());
        model.setName(req.getName());
        model.setCategory(req.getCategory());
        model.setVersion(1);
        model.setMetaInfo(buildMetaInfo(req, null, null));
        repositoryService.saveModel(model);

        // 初始化空 BPMN XML，否则 designer 打开会拿不到 source
        repositoryService.addModelEditorSource(
                model.getId(),
                EMPTY_BPMN_XML.getBytes(StandardCharsets.UTF_8));

        log.info("workflow.model.created id={} key={} by={}",
                model.getId(), req.getKey(), SecurityContextUtil.getUserId());
        return CommonResult.success(toVo(model));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<WorkflowModelVO> updateModel(String id, UpdateModelRequest req) {
        Model m = repositoryService.getModel(id);
        if (m == null) {
            throw new BusinessException("模型不存在: " + id);
        }
        if (req.getName() != null) {
            m.setName(req.getName());
        }
        if (req.getCategory() != null) {
            m.setCategory(req.getCategory());
        }
        // META_INFO_ 重建（保留原 schemaVersion 和 creator 信息）
        m.setMetaInfo(buildMetaInfo(null, req, parseMeta(m.getMetaInfo())));
        repositoryService.saveModel(m);
        return CommonResult.success(toVo(m));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> deleteModel(String id) {
        Model m = repositoryService.getModel(id);
        if (m == null) {
            throw new BusinessException("模型不存在: " + id);
        }
        repositoryService.deleteModel(id);
        log.info("workflow.model.deleted id={} by={}", id, SecurityContextUtil.getUserId());
        return CommonResult.success();
    }

    @Override
    public CommonResult<WorkflowModelVO> cloneModel(String id, String newName) {
        Model src = repositoryService.getModel(id);
        if (src == null) {
            throw new BusinessException("模型不存在: " + id);
        }
        byte[] xml = repositoryService.getModelEditorSource(id);

        // 新 key 用 src_key_clone_<ts> 避免冲突
        String newKey = src.getKey() + "_clone_" + System.currentTimeMillis();
        Model copy = repositoryService.newModel();
        copy.setKey(newKey);
        copy.setName(newName);
        copy.setCategory(src.getCategory());
        copy.setVersion(1);
        copy.setMetaInfo(buildMetaInfo(
                null,
                null,
                parseMeta(src.getMetaInfo())));
        repositoryService.saveModel(copy);

        if (xml != null && xml.length > 0) {
            repositoryService.addModelEditorSource(copy.getId(), xml);
        } else {
            repositoryService.addModelEditorSource(copy.getId(),
                    EMPTY_BPMN_XML.getBytes(StandardCharsets.UTF_8));
        }
        return CommonResult.success(toVo(copy));
    }

    // ===================== BPMN XML =====================

    @Override
    public CommonResult<String> getBpmnXml(String id) {
        Model m = repositoryService.getModel(id);
        if (m == null) {
            throw new BusinessException("模型不存在: " + id);
        }
        byte[] xml = repositoryService.getModelEditorSource(id);
        if (xml == null || xml.length == 0) {
            return CommonResult.success(EMPTY_BPMN_XML);
        }
        return CommonResult.success(new String(xml, StandardCharsets.UTF_8));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> saveBpmnXml(String id, SaveBpmnRequest req) {
        Model m = repositoryService.getModel(id);
        if (m == null) {
            throw new BusinessException("模型不存在: " + id);
        }

        // 命名空间净化：camunda: → flowable:（Flowable 引擎两种 prefix 都能读，
        // 但仓库 XML 保持 flowable: 一致性，便于后续 grep 与工具链识别）
        String xml = req.getXml() == null ? "" : req.getXml().replace("camunda:", "flowable:");

        // 预校验：解析失败尽早抛出，避免部署时才暴露
        try {
            BpmnXMLConverter converter = new BpmnXMLConverter();
            javax.xml.stream.XMLInputFactory xif = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLStreamReader xtr = xif.createXMLStreamReader(
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            BpmnModel parsed = converter.convertToBpmnModel(xtr);
            if (parsed == null || parsed.getProcesses() == null || parsed.getProcesses().isEmpty()) {
                return CommonResult.validateFailed("BPMN 内容为空或不含 process 节点");
            }
        } catch (Exception e) {
            log.warn("workflow.model.saveBpmn.invalid id={} cause={}", id, e.getMessage());
            return CommonResult.validateFailed("BPMN XML 解析失败: " + e.getMessage());
        }

        repositoryService.addModelEditorSource(id,
                xml.getBytes(StandardCharsets.UTF_8));
        if (req.getSvg() != null && !req.getSvg().isBlank()) {
            repositoryService.addModelEditorSourceExtra(id,
                    req.getSvg().getBytes(StandardCharsets.UTF_8));
        }
        return CommonResult.success();
    }

    // ===================== 部署 =====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<DeployModelResultVO> deployModel(String id) {
        Model m = repositoryService.getModel(id);
        if (m == null) {
            throw new BusinessException("模型不存在: " + id);
        }
        byte[] xml = repositoryService.getModelEditorSource(id);
        if (xml == null || xml.length == 0) {
            return CommonResult.validateFailed("模型尚未保存 BPMN 内容，请先保存");
        }

        try {
            String resourceName = m.getKey() + ".bpmn20.xml";
            DeploymentBuilder builder = repositoryService.createDeployment()
                    .name(m.getName())
                    .category(m.getCategory())
                    .addBytes(resourceName, xml);
            Deployment deploy = builder.deploy();

            // 回填 deploymentId
            m.setDeploymentId(deploy.getId());
            repositoryService.saveModel(m);

            List<ProcessDefinition> defs = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deploy.getId())
                    .list();

            DeployModelResultVO vo = new DeployModelResultVO();
            vo.setDeploymentId(deploy.getId());
            vo.setDeployedProcessDefinitionIds(defs.stream()
                    .map(ProcessDefinition::getId)
                    .collect(Collectors.toList()));
            vo.setVersion(defs.isEmpty() ? 0 : defs.get(0).getVersion());

            log.info("workflow.model.deployed id={} key={} deploymentId={} procDefs={}",
                    id, m.getKey(), deploy.getId(),
                    vo.getDeployedProcessDefinitionIds());
            return CommonResult.success(vo);
        } catch (Exception e) {
            log.error("workflow.model.deploy.failed id={}", id, e);
            return CommonResult.failed("部署失败: " + e.getMessage());
        }
    }

    // ===================== 辅助：META_INFO_ 与转换 =====================

    /** 构建 META_INFO_ JSON 串。create 路径下 req 入参为 CreateModelRequest。 */
    private String buildMetaInfo(CreateModelRequest createReq,
                                 UpdateModelRequest updateReq,
                                 Map<String, Object> oldMeta) {
        Map<String, Object> meta = new LinkedHashMap<>();
        if (oldMeta != null) {
            meta.putAll(oldMeta);
        }
        meta.put("version", META_VERSION);
        if (createReq != null) {
            meta.put("businessType", createReq.getBusinessType());
            meta.put("description", createReq.getDescription());
            meta.put("creatorId", SecurityContextUtil.getUserId());
            meta.put("creatorName", SecurityContextUtil.getUsername());
            meta.put("createTime", LocalDateTime.now().toString());
        }
        if (updateReq != null) {
            if (updateReq.getBusinessType() != null) {
                meta.put("businessType", updateReq.getBusinessType());
            }
            if (updateReq.getDescription() != null) {
                meta.put("description", updateReq.getDescription());
            }
            meta.put("lastModifyTime", LocalDateTime.now().toString());
            meta.put("lastModifyUserId", SecurityContextUtil.getUserId());
            meta.put("lastModifyUserName", SecurityContextUtil.getUsername());
        }
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new BusinessException("序列化 META_INFO_ 失败", e);
        }
    }

    private Map<String, Object> parseMeta(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (Exception e) {
            log.warn("parseMeta 失败，返回空: {}", e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    private WorkflowModelVO toVo(Model m) {
        WorkflowModelVO vo = new WorkflowModelVO();
        vo.setId(m.getId());
        vo.setName(m.getName());
        vo.setKey(m.getKey());
        vo.setCategory(m.getCategory());
        vo.setVersion(m.getVersion());
        vo.setMetaInfo(m.getMetaInfo());
        vo.setDeploymentId(m.getDeploymentId());
        vo.setTenantId(m.getTenantId());
        vo.setCreateTime(toLocalDateTime(m.getCreateTime()));
        vo.setLastUpdateTime(toLocalDateTime(m.getLastUpdateTime()));

        Map<String, Object> meta = parseMeta(m.getMetaInfo());
        if (meta.get("businessType") != null) {
            vo.setBusinessType(String.valueOf(meta.get("businessType")));
        }
        if (meta.get("description") != null) {
            vo.setDescription(String.valueOf(meta.get("description")));
        }
        if (meta.get("creatorId") != null) {
            vo.setCreatorId(toLong(meta.get("creatorId")));
        }
        if (meta.get("creatorName") != null) {
            vo.setCreatorName(String.valueOf(meta.get("creatorName")));
        }
        if (meta.get("version") != null) {
            vo.setMetaVersion(toInt(meta.get("version")));
        }
        return vo;
    }

    private WorkflowModelListItemVO toListItemVo(Model m) {
        WorkflowModelListItemVO vo = new WorkflowModelListItemVO();
        vo.setId(m.getId());
        vo.setName(m.getName());
        vo.setKey(m.getKey());
        vo.setCategory(m.getCategory());
        vo.setVersion(m.getVersion());
        vo.setDeploymentId(m.getDeploymentId());
        vo.setCreateTime(toLocalDateTime(m.getCreateTime()));
        vo.setLastUpdateTime(toLocalDateTime(m.getLastUpdateTime()));

        Map<String, Object> meta = parseMeta(m.getMetaInfo());
        if (meta.get("businessType") != null) {
            vo.setBusinessType(String.valueOf(meta.get("businessType")));
        }
        if (meta.get("description") != null) {
            vo.setDescription(String.valueOf(meta.get("description")));
        }
        if (meta.get("creatorName") != null) {
            vo.setCreatorName(String.valueOf(meta.get("creatorName")));
        }
        return vo;
    }

    private static Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }

    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }

    /** Flowable Model 的 createTime/lastUpdateTime 是 java.util.Date，转 LocalDateTime 给 VO。 */
    private static LocalDateTime toLocalDateTime(java.util.Date d) {
        return d == null ? null : d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    /** 预留：未来 businessType 二级过滤若下沉到引擎层，可用此钩子。 */
    @SuppressWarnings("unused")
    private List<ProcessDefinition> safeQueryProcDefs(String deploymentId) {
        ProcessDefinitionQuery q = repositoryService.createProcessDefinitionQuery();
        return q.deploymentId(deploymentId).list() == null
                ? new ArrayList<>()
                : q.deploymentId(deploymentId).list();
    }
}