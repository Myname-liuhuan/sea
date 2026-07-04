package com.example.sea.workflow.api.feign.fallback;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.dto.ResetPasswordRequest;
import com.example.sea.workflow.api.feign.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * sea-system Feign 降级工厂。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Slf4j
@Component
public class SystemFeignClientFallBack implements FallbackFactory<SystemFeignClient> {

    @Override
    public SystemFeignClient create(Throwable cause) {
        return new SystemFeignClient() {
            @Override
            public CommonResult<Map<String, Object>> getUserRaw(Long userId) {
                log.error("getUserRaw failed target={} cause={}", userId, cause.getMessage());
                return CommonResult.failed("sea-system 不可用");
            }

            @Override
            public CommonResult<Long> getUserLeaderId(Long userId) {
                log.error("getUserLeaderId failed target={} cause={}", userId, cause.getMessage());
                return CommonResult.failed("sea-system 不可用");
            }

            @Override
            public CommonResult<Void> resetPassword(Long userId, Boolean requirePasswordChange, ResetPasswordRequest body) {
                log.error("resetPassword failed target={} cause={}", userId, cause.getMessage());
                return CommonResult.failed("sea-system 不可用");
            }
        };
    }
}
