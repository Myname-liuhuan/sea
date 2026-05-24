package com.example.sea.system.api.feign;

import com.example.sea.system.api.feign.fallback.SystemFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;

/**
 * 系统服务FeignClient
 * 提供用户信息查询等接口
 */
@FeignClient(value = "sea-system", fallbackFactory = SystemFeignClientFallBack.class)
public interface SystemFeignClient {

    /**
     * 获取登录用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/sysUser/getLoginUser")
    CommonResult<LoginUser> getLoginUser(@RequestParam String username);

}