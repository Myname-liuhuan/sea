package com.example.sea.system.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.log.api.dto.LoginLogDTO;
import com.example.sea.system.service.ISysLoginLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 登录日志控制器
 * @author liuhuan
 * @date 2026-05-18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysLoginLog")
@Tag(name = "登录日志管理", description = "系统登录日志相关操作接口")
public class SysLoginLogController {

    private final ISysLoginLogService sysLoginLogService;

    /**
     * 记录登录日志
     * @param loginLogDTO 登录日志信息
     * @return 操作结果
     */
    @PostMapping("/record")
    @Operation(summary = "记录登录日志", description = "记录用户的登录日志信息")
    public CommonResult<Void> recordLoginLog(@RequestBody LoginLogDTO loginLogDTO) {
        sysLoginLogService.recordLoginLog(loginLogDTO);
        return CommonResult.success(null);
    }
}