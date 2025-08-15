package com.example.sea.auth.feign.fallback;

import com.example.sea.auth.feign.SystemFeignClient;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }
}
