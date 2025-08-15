package com.example.sea.auth.feign;

import com.example.sea.auth.feign.fallback.SystemFeignClientFallback;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统服务Feign客户端
 * @author liuhuan
 * @date 2025-08-15
 */
@FeignClient(name = "sea-system", fallbackFactory = SystemFeignClientFallback.class)
public interface SystemFeignClient {

    /**
     * 根据用户名获取登录用户信息
     * @param username 用户名
     * @return 登录用户信息
     */
    @PostMapping("/sysUser/getLoginUser")
    CommonResult<LoginUser> getLoginUser(@RequestBody String username);
}
