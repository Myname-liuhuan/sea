package com.example.sea.system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.log.api.dto.OperationLogDTO;
import com.example.sea.system.converter.SysOperationLogConverter;
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
    private final SysOperationLogConverter operationLogConverter;

    /**
     * 保存操作日志
     * @param dto 操作日志信息
     * @return 操作结果
     */
    @PostMapping("/save")
    @Operation(summary = "保存操作日志", description = "保存系统操作日志信息")
    public CommonResult<Void> saveOperationLog(@RequestBody OperationLogDTO dto) {
        SysOperationLogPO po = operationLogConverter.dtoToPo(dto);
        operationLogService.save(po);
        return CommonResult.success();
    }
}