package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.utils.RedisUtil;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.dto.ApplyResultDTO;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.param.ApplyRequest;
import com.example.sea.workflow.constants.WorkflowStatusEnum;
import com.example.sea.workflow.converter.WorkflowTaskConverter;
import com.example.sea.workflow.dao.WorkflowTaskMapper;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import com.example.sea.workflow.service.IWorkflowApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 申请发起实现。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowApplyServiceImpl implements IWorkflowApplyService {

    private static final String BUSINESS_TYPE = "PASSWORD_RESET";
    private static final String PROCESS_DEFINITION_KEY = "reset_password";
    private static final String IDEMPOTENCY_KEY_PREFIX = "workflow:apply:idemp:";
    private static final long IDEMPOTENCY_TTL_SECONDS = 600L;

    private final WorkflowTaskMapper taskMapper;
    private final WorkflowTaskConverter taskConverter;
    private final SystemFeignClient systemFeignClient;
    private final RuntimeService runtimeService;
    private final RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<ApplyResultDTO> apply(ApplyRequest request, String idempotencyKey) {
        Long applicantId = SecurityContextUtil.getUserId();
        if (applicantId == null) {
            return CommonResult.failed("未登录或会话已失效");
        }

        // 1. Idempotency 兜底
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            String cacheKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            Object cached = redisUtil.get(cacheKey);
            if (cached instanceof String s) {
                return CommonResult.success(new ApplyResultDTO(s, null));
            }
        }

        // 2. 取目标用户信息（由 sea-system 提供 Feign）
        Map<String, Object> userMap = systemFeignClient.getUserRaw(request.getTargetUserId());
        if (userMap == null || userMap.get("id") == null) {
            return CommonResult.failed("目标用户不存在");
        }
        Long deptId = toLong(userMap.get("deptId"));
        Integer level = toInt(userMap.get("level"));
        if (level == null) {
            level = 5;
        }

        // 3. 持久化工单
        WorkflowTaskPO task = new WorkflowTaskPO();
        task.setTaskNo(generateTaskNo());
        task.setBusinessType(BUSINESS_TYPE);
        task.setBusinessKey(String.valueOf(request.getTargetUserId()));
        task.setApplicantId(applicantId);
        task.setTargetUserId(request.getTargetUserId());
        task.setReason(request.getReason());
        task.setUrgency(request.getUrgency());
        task.setStatus(WorkflowStatusEnum.PENDING.getCode());
        task.setVersion(0);
        task.setDelFlag(0);

        // 4. 启动 Flowable 实例
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", applicantId);
        variables.put("targetUserId", request.getTargetUserId());
        variables.put("targetUserLevel", level);
        variables.put("targetDeptId", deptId);
        variables.put("reason", request.getReason());
        variables.put("urgency", request.getUrgency());

        ProcessInstance pi = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY, task.getTaskNo(), variables);

        task.setFlowInstanceId(pi.getId());
        task.setStatus(WorkflowStatusEnum.IN_PROGRESS.getCode());
        task.setCurrentNode("dept_leader");
        taskMapper.insert(task);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            redisUtil.setStr(IDEMPOTENCY_KEY_PREFIX + idempotencyKey,
                    task.getTaskNo(), IDEMPOTENCY_TTL_SECONDS);
        }
        log.info("workflow.apply applicant={} target={} taskNo={} flowInstance={}",
                applicantId, request.getTargetUserId(), task.getTaskNo(), pi.getId());
        return CommonResult.success(new ApplyResultDTO(task.getTaskNo(), task.getId()));
    }

    private static String generateTaskNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "WR" + date + rand;
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
}
