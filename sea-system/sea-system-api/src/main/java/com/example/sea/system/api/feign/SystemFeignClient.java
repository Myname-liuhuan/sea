package com.example.sea.system.api.feign;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.feign.fallback.SystemFeignClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sea.common.security.entity.LoginUser;

/**
 * 系统服务FeignClient
 * 提供用户信息查询等接口
 */
@FeignClient(value = "sea-system", contextId = "systemFeignClient", fallbackFactory = SystemFeignClientFallBack.class)
public interface SystemFeignClient {

    /**
     * 取鉴权用 LoginUser（含 password hash），供 sea-auth 走 BCrypt 校验。
     * 走内部端点 /api/system/users/{username}/auth-info，需 internal:callback 权限。
     * @param username 用户名
     * @return 含 password 的完整 LoginUser
     */
    @GetMapping("/api/system/users/{username}/auth-info")
    CommonResult<LoginUser> getLoginUser(@PathVariable("username") String username);

    /**
     * 改密：服务端用 oldPassword BCrypt 比对后写入新密码并清 require_password_change。
     * oldPassword 可空（强制改密首登场景，sea-auth 已知密码正确可绕过校验）。
     */
    @GetMapping("/sysUser/changePassword")
    CommonResult<Boolean> changePassword(@RequestParam("userId") Long userId,
                                        @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                        @RequestParam("newPassword") String newPassword);

}
