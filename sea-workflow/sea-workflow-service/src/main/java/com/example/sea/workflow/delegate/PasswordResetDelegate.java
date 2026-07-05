package com.example.sea.workflow.delegate;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.workflow.api.feign.NotifyFeignClient;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.dto.ResetPasswordRequest;
import com.example.sea.workflow.dao.WorkflowTaskMapper;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.workflow.constants.WorkflowStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 重置密码执行节点。
 *
 * <p>行为：
 * <ol>
 *   <li>读取流程变量 targetUserId</li>
 *   <li>生成 8 位 Base62 随机密码（char[]，用完清零）</li>
 *   <li>调用 sea-system Feign /api/system/users/{id}/reset-password，
 *       requirePasswordChange=true</li>
 *   <li>取用户 email/mobile，调 NotifyFeignClient.send PWD_RESET_OK
 *       （in-app → email → sms 降级）</li>
 *   <li>落 workflow_task.status = COMPLETED（避免 UI 一直停留在"已通过"）</li>
 *   <li>设置流程变量 executionDone=true</li>
 * </ol>
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component("passwordResetDelegate")
@RequiredArgsConstructor
public class PasswordResetDelegate implements JavaDelegate {

    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 8;

    private final SystemFeignClient systemFeignClient;
    private final NotifyFeignClient notifyFeignClient;
    private final WorkflowTaskMapper taskMapper;

    @Override
    public void execute(DelegateExecution execution) {
        Object targetUserIdObj = execution.getVariable("targetUserId");
        if (targetUserIdObj == null) {
            throw new BusinessException("流程变量 targetUserId 缺失");
        }
        Long targetUserId = ((Number) targetUserIdObj).longValue();
        String taskNo = (String) execution.getProcessInstanceBusinessKey();

        // 用 char[] 不进 String pool；execute 末尾 Arrays.fill 清零
        char[] pwdChars = randomPassword(PASSWORD_LENGTH);
        String tempPassword = new String(pwdChars);

        try {
            ResetPasswordRequest body = new ResetPasswordRequest();
            body.setNewPassword(tempPassword);
            body.setRequirePasswordChange(Boolean.TRUE);

            var resp = systemFeignClient.resetPassword(targetUserId, Boolean.TRUE, body);
            if (resp == null || !resp.isSuccess()) {
                log.error("密码重置失败 target={} resp={}", targetUserId, resp);
                throw new BusinessException("密码重置调用失败");
            }

            // 取 email/mobile 推送给用户
            String email = null;
            String mobile = null;
            try {
                var userResp = systemFeignClient.getUserRaw(targetUserId);
                if (userResp != null && userResp.isSuccess() && userResp.getData() != null) {
                    Object e = userResp.getData().get("email");
                    Object m = userResp.getData().get("mobile");
                    email = e == null ? null : e.toString();
                    mobile = m == null ? null : m.toString();
                }
            } catch (Exception e) {
                log.warn("取用户联络方式失败 target={}", targetUserId, e);
            }

            Map<String, Object> notifyPayload = new HashMap<>();
            notifyPayload.put("primaryChannel", "IN_APP");
            notifyPayload.put("fallbackChannels", List.of("EMAIL", "SMS"));
            notifyPayload.put("receiverUserId", targetUserId);
            notifyPayload.put("email", email);
            notifyPayload.put("mobile", mobile);
            notifyPayload.put("templateCode", "PWD_RESET_OK");
            Map<String, String> params = new HashMap<>();
            params.put("pwd", tempPassword);
            params.put("approver", "审批人");
            params.put("appName", "海纳系统");
            notifyPayload.put("params", params);
            notifyPayload.put("bizKey", taskNo);
            notifyPayload.put("appName", "海纳系统");

            try {
                Map<String, Object> notifyResp = notifyFeignClient.send(notifyPayload);
                log.info("password.reset.notify target={} resp={}", targetUserId, notifyResp);
            } catch (Exception e) {
                // 通知失败不阻断流程（密码已成功重置）
                log.warn("password.reset.notify failed target={}", targetUserId, e);
            }

            // §14 #12：显式落 workflow_task.status = COMPLETED，
            // 否则 act_hi_* 完成但 workflow_task 留在 APPROVED，UI 一直等
            try {
                WorkflowTaskPO po = taskMapper.selectOne(
                        Wrappers.<WorkflowTaskPO>lambdaQuery()
                                .eq(WorkflowTaskPO::getTaskNo, taskNo)
                                .last("LIMIT 1"));
                if (po != null) {
                    po.setStatus(WorkflowStatusEnum.COMPLETED.getCode());
                    taskMapper.updateById(po);
                }
            } catch (Exception e) {
                log.warn("workflow_task.complete.update failed taskNo={}", taskNo, e);
            }

            execution.setVariable("executionDone", "true");
            log.info("password.reset.success target={} instance={}",
                    targetUserId, execution.getProcessInstanceId());
        } finally {
            // §14 #7：清零 char[] heap 残留，缩短密码在 JVM 留存
            Arrays.fill(pwdChars, '\0');
        }
    }

    /** 生成 Base62 随机密码字面量为 char[]，避免 String pool。 */
    static char[] randomPassword(int len) {
        SecureRandom random = new SecureRandom();
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = ALPHANUM.charAt(random.nextInt(ALPHANUM.length()));
        }
        return buf;
    }
}
