package com.example.sea.workflow.service;

import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.vo.WorkflowApprovalVO;
import com.example.sea.workflow.api.vo.WorkflowTaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把工单 / 审批记录里的 userId 占位反查 user_name，批量并入 VO。
 *
 * <p>避免在每一处 controller 内分别处理；统一一处集中调用 Feign。
 *
 * @author liuhuan
 * @date 2026-07-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowNameEnricher {

    private final SystemFeignClient systemFeignClient;

    /**
     * 给任务列表回填 applicantName / targetUserName。
     */
    public void enrichTaskNames(List<WorkflowTaskVO> tasks) {
        if (tasks == null || tasks.isEmpty()) return;
        Set<Long> ids = new HashSet<>();
        for (WorkflowTaskVO t : tasks) {
            if (t.getApplicantId() != null) ids.add(t.getApplicantId());
            if (t.getTargetUserId() != null) ids.add(t.getTargetUserId());
        }
        Map<Long, String> nameMap = batchLookup(ids);

        for (WorkflowTaskVO t : tasks) {
            if (t.getApplicantName() == null && t.getApplicantId() != null) {
                t.setApplicantName(nameMap.get(t.getApplicantId()));
            }
            if (t.getTargetUserName() == null && t.getTargetUserId() != null) {
                t.setTargetUserName(nameMap.get(t.getTargetUserId()));
            }
        }
    }

    /**
     * 给审批记录列表回填 approverName。
     */
    public void enrichApprovalNames(List<WorkflowApprovalVO> approvals) {
        if (approvals == null || approvals.isEmpty()) return;
        Set<Long> ids = new HashSet<>();
        for (WorkflowApprovalVO a : approvals) {
            if (a.getApproverId() != null) ids.add(a.getApproverId());
        }
        Map<Long, String> nameMap = batchLookup(ids);

        for (WorkflowApprovalVO a : approvals) {
            if (a.getApproverName() == null && a.getApproverId() != null) {
                a.setApproverName(nameMap.get(a.getApproverId()));
            }
        }
    }

    private Map<Long, String> batchLookup(Set<Long> ids) {
        Map<Long, String> map = new HashMap<>();
        for (Long id : ids) {
            try {
                var resp = systemFeignClient.getUserRaw(id);
                if (resp != null && resp.isSuccess() && resp.getData() != null) {
                    Object username = resp.getData().get("username");
                    if (username != null) map.put(id, username.toString());
                }
            } catch (Exception e) {
                // 单个失败不阻塞整批；缺失的留 null，UI fallback 显示 id
                log.warn("nameEnrich.lookup failed id={} cause={}", id, e.getMessage());
            }
        }
        return map;
    }
}
