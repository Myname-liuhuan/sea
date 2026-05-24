package com.example.sea.system.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

import com.example.sea.system.api.feign.fallback.LoginLogFeignClientFallBack;

/**
 * 登录日志Feign客户端接口（继承自sea-log-api）
 * @author liuhuan
 * @date 2026-05-18
 */
@FeignClient(value = "sea-system", fallbackFactory = LoginLogFeignClientFallBack.class)
public interface LoginLogFeignClient extends com.example.sea.log.api.feign.LoginLogFeignClient {
}