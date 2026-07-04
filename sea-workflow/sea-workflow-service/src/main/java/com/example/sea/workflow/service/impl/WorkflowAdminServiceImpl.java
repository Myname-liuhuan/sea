package com.example.sea.workflow.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import com.example.sea.workflow.api.param.AdminEmergencyRequest;
import com.example.sea.workflow.service.IWorkflowAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员通道：绕过审批直接重置密码。
 *
 * <p>独立审计记录（落地到 sea-log 的 admin_audit 表，M3 时接入）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowAdminServiceImpl implements IWorkflowAdminService {

    private final SystemFeignClient systemFeignClient;

    @Override
    public CommonResult<Void> emergencyReset(AdminEmergencyRequest request) {
        Long operatorId = SecurityContextUtil.getUserId();
        if (operatorId == null) {
            return CommonResult.failed("未登录");
        }

        CommonResult<Void> resp = systemFeignClient.resetPassword(
                request.getTargetUserId(), null, Boolean.TRUE);
        if (resp == null || !resp.isSuccess()) {
            log.warn("workflow.admin.emergency failed operator={} target={}",
                    operatorId, request.getTargetUserId());
            return CommonResult.failed("紧急重置失败");
        }
        log.warn("workflow.admin.emergency operator={} target={} reason={}",
                operatorId, request.getTargetUserId(), request.getReason());
        return CommonResult.success();
    }
}
