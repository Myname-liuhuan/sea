package com.example.sea.system.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.dto.OperationLogDTO;
import com.example.sea.system.api.feign.fallback.OperationLogFeignClientFallBack;

/**
 * 操作日志Feign客户端接口
 * @author liuhuan
 * @date 2026-05-18
 */
@FeignClient(value = "sea-system", fallbackFactory = OperationLogFeignClientFallBack.class)
public interface OperationLogFeignClient {

    /**
     * 保存操作日志
     * @param dto 操作日志信息
     * @return 操作结果
     */
    @PostMapping("/sysOperationLog/save")
    CommonResult<Void> saveOperationLog(OperationLogDTO dto);
}
