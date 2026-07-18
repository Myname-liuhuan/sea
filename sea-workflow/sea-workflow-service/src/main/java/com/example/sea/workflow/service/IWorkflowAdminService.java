package com.example.sea.workflow.service;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.dto.AdminEmergencyRequest;

/**
 * 管理员紧急通道 + 流程定义管理（M2）。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
public interface IWorkflowAdminService {

    /**
     * 紧急通道：绕过审批直接重置密码。
     *
     * <p>独立审计，独立接口，调用记录写入 sea-log 的 admin_audit 表。
     */
    CommonResult<Void> emergencyReset(AdminEmergencyRequest request);
}
