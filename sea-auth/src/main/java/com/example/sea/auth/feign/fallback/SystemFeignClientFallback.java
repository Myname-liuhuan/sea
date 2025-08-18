package com.example.sea.auth.feign.fallback;

import com.example.sea.auth.feign.SystemFeignClient;
import com.example.sea.common.core.result.CommonResult;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * SystemFeignClient的fallback处理类
 * @author liuhuan
 * @date 2025-08-15
 */
@Component
public class SystemFeignClientFallback implements FallbackFactory<SystemFeignClient>  {

    @Override
    public SystemFeignClient create(Throwable cause) {
        return new SystemFeignClient() {
            @Override
            public CommonResult<com.example.sea.common.security.entity.LoginUser> getLoginUser(String username) {
                // 可根据 cause 打印日志
                return CommonResult.failed("feign调用system模块失败");
            }
        };
    }
}
