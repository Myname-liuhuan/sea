package com.example.sea.workflow.delegate;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 重置密码执行节点。
 *
 * <p>行为：
 * <ol>
 *   <li>读取流程变量 targetUserId</li>
 *   <li>生成 8 位 Base62 随机密码</li>
 *   <li>调用 sea-system Feign /api/system/users/{id}/reset-password，
 *       requirePasswordChange=true</li>
 *   <li>设置流程变量 executionDone=true，供 End 前的 listener（如果需要）检查</li>
 * </ol>
 *
 * <p>提交通知的功能留给 M3（{@code NotifyFeignClient.send}）。
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

    @Override
    public void execute(DelegateExecution execution) {
        Object targetUserIdObj = execution.getVariable("targetUserId");
        if (targetUserIdObj == null) {
            throw new BusinessException("流程变量 targetUserId 缺失");
        }
        Long targetUserId = ((Number) targetUserIdObj).longValue();
        String tempPassword = randomPassword(PASSWORD_LENGTH);

        ResetPasswordRequest body = new ResetPasswordRequest();
        body.setNewPassword(tempPassword);
        body.setRequirePasswordChange(Boolean.TRUE);

        var resp = systemFeignClient.resetPassword(targetUserId, Boolean.TRUE, body);
        if (resp == null || !resp.isSuccess()) {
            log.error("密码重置失败 target={} resp={}", targetUserId, resp);
            throw new BusinessException("密码重置调用失败");
        }
        execution.setVariable("executionDone", "true");
        // TODO M3：调用 NotifyFeignClient.send 把 tempPassword 推送给目标用户
        log.info("password.reset.success target={} instance={}",
                targetUserId, execution.getProcessInstanceId());
    }

    static String randomPassword(int len) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
