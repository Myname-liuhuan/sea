package com.example.sea.auth.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.example.sea.auth.feign.SystemFeignClient;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class SystemFeignClientFallBack implements FallbackFactory<SystemFeignClient> {

    @Override
    public SystemFeignClient create(Throwable cause) {
        return new SystemFeignClient() {
            @Override
            public CommonResult<LoginUser> getLoginUser(String username) {
                log.error("调用sea-system服务失败: {}", cause.getMessage());
                return CommonResult.failed("无法获取用户信息: " + cause.getMessage());
            }
            
        };
    }
    
}
