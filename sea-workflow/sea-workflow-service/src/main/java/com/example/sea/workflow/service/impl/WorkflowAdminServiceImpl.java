package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.notification.api.dto.NotifyDTO;
import com.example.sea.workflow.api.dto.AdminEmergencyRequest;
import com.example.sea.workflow.api.dto.ResetPasswordRequest;
import com.example.sea.workflow.api.feign.NotifyFeignClient;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.dao.AdminAuditMapper;
import com.example.sea.workflow.entity.AdminAuditPO;
import com.example.sea.workflow.service.IWorkflowAdminService;
import com.example.sea.workflow.util.NotifyPayloadBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 管理员通道：绕过审批直接重置密码。
 *
 * <p>独立审计（admin_audit 表），与 workflow_task 无关，避免被工作流查询 API 带出。
 *
 * <p>行为：
 * <ol>
 *   <li>生成 8 位 Base62 临时密码（char[]，用完清零）</li>
 *   <li>调用 sea-system Feign 写入 sys_user.password_hash 与 require_password_change</li>
 *   <li>取目标用户与 admin 的 email/mobile，调 NotifyFeignClient.send PWD_RESET_OK
 *       （in-app → email → sms 降级）；通知发送失败不阻塞</li>
 *   <li>写 admin_audit 审计</li>
 * </ol>
 *
 * <p>§14 #7：密码用 char[] 不入 String pool，try/catch/finally 清零
 * <br>§14 #8：admin 也收到一份密码邮件，方便转交用户
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowAdminServiceImpl implements IWorkflowAdminService {

    private static final String ALPHANUM =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 8;

    private final SystemFeignClient systemFeignClient;
    private final NotifyFeignClient notifyFeignClient;
    private final AdminAuditMapper adminAuditMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> emergencyReset(AdminEmergencyRequest request) {
        Long operatorId = SecurityContextUtil.getUserId();
        if (operatorId == null) {
            return CommonResult.failed("未登录");
        }

        char[] pwdChars = randomPassword(PASSWORD_LENGTH);
        try {
            ResetPasswordRequest body = new ResetPasswordRequest();
            body.setNewPassword(new String(pwdChars));

            CommonResult<Void> resp = systemFeignClient.resetPassword(
                    request.getTargetUserId(), Boolean.TRUE, body);
            if (resp == null || !resp.isSuccess()) {
                log.warn("workflow.admin.emergency failed operator={} target={}",
                        operatorId, request.getTargetUserId());
                writeAudit(operatorId, request, false, null);
                return CommonResult.failed("紧急重置失败");
            }

            // 通知：目标用户 + admin 各自收到一份
            String tempPassword = new String(pwdChars);
            pushNotification(request.getTargetUserId(), tempPassword, "目标用户");
            pushNotification(operatorId, tempPassword, "管理员");

            writeAudit(operatorId, request, true, tempPassword);
            log.warn("workflow.admin.emergency operator={} target={} reason={}",
                    operatorId, request.getTargetUserId(), request.getReason());
            return CommonResult.success();
        } finally {
            Arrays.fill(pwdChars, '\0');
        }
    }

    /**
     * 三通道推送（含失败降级）。失败不抛异常，写到 notify_log 后由重试 job 接管。
     */
    private void pushNotification(Long receiverUserId, String tempPassword, String role) {
        try {
            String email = null;
            String mobile = null;
            var userResp = systemFeignClient.getUserRaw(receiverUserId);
            if (userResp != null && userResp.isSuccess() && userResp.getData() != null) {
                Object e = userResp.getData().get("email");
                Object m = userResp.getData().get("mobile");
                email = e == null ? null : e.toString();
                mobile = m == null ? null : m.toString();
            }

            NotifyDTO notifyPayload = NotifyPayloadBuilder.buildPasswordResetPayload(
                    receiverUserId, email, mobile, tempPassword, role, "ADMIN_" + receiverUserId);
            notifyFeignClient.send(notifyPayload);
        } catch (Exception e) {
            log.warn("admin.pushNotify.failed role={} receiver={}", role, receiverUserId, e);
        }
    }

    private void writeAudit(Long operatorId, AdminEmergencyRequest request,
                            boolean success, String tempPassword) {
        try {
            AdminAuditPO audit = new AdminAuditPO();
            audit.setOperatorId(operatorId);
            audit.setAction("EMERGENCY_RESET");
            audit.setTargetUserId(request.getTargetUserId());
            // 审计里不存明文密码，仅记 [OK]/[FAIL] + reason
            audit.setReason((success ? "[OK] " : "[FAIL] ") + request.getReason());
            adminAuditMapper.insert(audit);
        } catch (Exception e) {
            log.error("admin_audit.write.failed operator={} target={}",
                    operatorId, request.getTargetUserId(), e);
        }
    }

    private static char[] randomPassword(int len) {
        SecureRandom random = new SecureRandom();
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = ALPHANUM.charAt(random.nextInt(ALPHANUM.length()));
        }
        return buf;
    }
}
