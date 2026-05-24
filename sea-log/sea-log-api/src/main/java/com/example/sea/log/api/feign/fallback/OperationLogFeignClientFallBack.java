package com.example.sea.log.api.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.log.api.dto.OperationLogDTO;
import com.example.sea.log.api.feign.OperationLogFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志Feign客户端降级处理工厂
 * @author liuhuan
 * @date 2026-05-18
 */
@Slf4j
@Component
public class OperationLogFeignClientFallBack implements FallbackFactory<OperationLogFeignClient> {

    @Override
    public OperationLogFeignClient create(Throwable cause) {
        return new OperationLogFeignClient() {
            @Override
            public CommonResult<Void> saveOperationLog(OperationLogDTO dto) {
                log.error("保存操作日志失败: {}", cause.getMessage());
                return CommonResult.failed("保存操作日志失败: " + cause.getMessage());
            }
        };
    }
}