package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.param.AdminEmergencyRequest;
import com.example.sea.workflow.dao.AdminAuditMapper;
import com.example.sea.workflow.entity.AdminAuditPO;
import com.example.sea.workflow.service.IWorkflowAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员通道：绕过审批直接重置密码。
 *
 * <p>独立审计（admin_audit 表），与 workflow_task 无关，避免被工作流查询 API 带出。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowAdminServiceImpl implements IWorkflowAdminService {

    private final SystemFeignClient systemFeignClient;
    private final AdminAuditMapper adminAuditMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> emergencyReset(AdminEmergencyRequest request) {
        Long operatorId = SecurityContextUtil.getUserId();
        if (operatorId == null) {
            return CommonResult.failed("未登录");
        }

        com.example.sea.workflow.api.dto.ResetPasswordRequest body =
                new com.example.sea.workflow.api.dto.ResetPasswordRequest();
        body.setNewPassword(generateTempPassword());
        CommonResult<Void> resp = systemFeignClient.resetPassword(
                request.getTargetUserId(), Boolean.TRUE, body);
        if (resp == null || !resp.isSuccess()) {
            log.warn("workflow.admin.emergency failed operator={} target={}",
                    operatorId, request.getTargetUserId());
            writeAudit(operatorId, request, false);
            return CommonResult.failed("紧急重置失败");
        }

        writeAudit(operatorId, request, true);
        log.warn("workflow.admin.emergency operator={} target={} reason={}",
                operatorId, request.getTargetUserId(), request.getReason());
        return CommonResult.success();
    }

    private void writeAudit(Long operatorId, AdminEmergencyRequest request, boolean success) {
        try {
            AdminAuditPO audit = new AdminAuditPO();
            audit.setOperatorId(operatorId);
            audit.setAction("EMERGENCY_RESET");
            audit.setTargetUserId(request.getTargetUserId());
            audit.setReason((success ? "[OK] " : "[FAIL] ") + request.getReason());
            adminAuditMapper.insert(audit);
        } catch (Exception e) {
            log.error("admin_audit.write.failed operator={} target={}",
                    operatorId, request.getTargetUserId(), e);
        }
    }

    private static final String ALPHANUM =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private static String generateTempPassword() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
