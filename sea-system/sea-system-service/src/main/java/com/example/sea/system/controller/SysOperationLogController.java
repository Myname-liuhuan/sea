package com.example.sea.system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.log.api.dto.OperationLogDTO;
import com.example.sea.system.entity.SysOperationLogPO;
import com.example.sea.system.service.ISysOperationLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 操作日志控制器
 * @author liuhuan
 * @date 2026-05-18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysOperationLog")
@Tag(name = "操作日志管理", description = "系统操作日志相关操作接口")
public class SysOperationLogController {

    private final ISysOperationLogService operationLogService;

    /**
     * 保存操作日志
     * @param dto 操作日志信息
     * @return 操作结果
     */
    @PostMapping("/save")
    @Operation(summary = "保存操作日志", description = "保存系统操作日志信息")
    public CommonResult<Void> saveOperationLog(@RequestBody OperationLogDTO dto) {
        SysOperationLogPO po = convert(dto);
        operationLogService.save(po);
        return CommonResult.success();
    }

    /**
     * 将DTO转换为PO
     * @param dto 操作日志DTO
     * @return 操作日志PO
     */
    private SysOperationLogPO convert(OperationLogDTO dto) {
        SysOperationLogPO po = new SysOperationLogPO();
        po.setTitle(dto.getTitle());
        po.setBusinessType(dto.getBusinessType());
        po.setMethod(dto.getMethod());
        po.setRequestMethod(dto.getRequestMethod());
        po.setOperatorType(dto.getOperatorType());
        po.setUserId(dto.getUserId());
        po.setUsername(dto.getUsername());
        po.setOperationUrl(dto.getOperationUrl());
        po.setOperationIp(dto.getOperationIp());
        po.setOperationLocation(dto.getOperationLocation());
        po.setOperationParam(dto.getOperationParam());
        po.setResponseParam(dto.getResponseParam());
        po.setStatus(dto.getStatus());
        po.setErrorMsg(dto.getErrorMsg());
        po.setDuration(dto.getDuration());
        po.setOperationTime(dto.getOperationTime());
        return po;
    }
}