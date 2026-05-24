package com.example.sea.log.api.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.log.api.dto.LoginLogDTO;
import com.example.sea.log.api.feign.LoginLogFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录日志Feign客户端降级处理工厂
 * @author liuhuan
 * @date 2026-05-18
 */
@Slf4j
@Component
public class LoginLogFeignClientFallBack implements FallbackFactory<LoginLogFeignClient> {

    @Override
    public LoginLogFeignClient create(Throwable cause) {
        return new LoginLogFeignClient() {
            @Override
            public CommonResult<Void> recordLoginLog(LoginLogDTO loginLogDTO) {
                log.error("记录登录日志失败: {}", cause.getMessage());
                return CommonResult.failed("记录登录日志失败: " + cause.getMessage());
            }
        };
    }
}