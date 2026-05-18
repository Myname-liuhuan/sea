package com.example.sea.system.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.dto.LoginLogDTO;
import com.example.sea.system.api.feign.fallback.LoginLogFeignClientFallBack;

/**
 * 登录日志Feign客户端接口
 * @author liuhuan
 * @date 2026-05-18
 */
@FeignClient(value = "sea-system", fallbackFactory = LoginLogFeignClientFallBack.class)
public interface LoginLogFeignClient {

    /**
     * 记录登录日志
     * @param loginLogDTO 登录日志信息
     * @return 操作结果
     */
    @PostMapping("/sysLoginLog/record")
    CommonResult<Void> recordLoginLog(LoginLogDTO loginLogDTO);
}