package com.example.sea.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.sea.auth.feign.fallback.SystemFeignClientFallBack;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;

/**
 * Feign 的参数处理规则大致是这样的（Spring Cloud OpenFeign 集成后）：
 * 1. 如果参数有注解，Feign 会根据注解的类型来决定如何处理这个参数。
 * 2. 如果没有注解，Feign 会默认把这个参数当成 RequestBody（即发送 POST 请求，body 里包含参数）。
 * 常见的注解有：
 * - @RequestParam：参数会拼到 URL 的 query string，比如 ?username=xxx。
 * - @PathVariable：参数会替换到 URL 路径里，比如 /user/{id} → /user/1。
 * - @RequestBody：参数会序列化成 JSON 放进 body 里。
 */
@FeignClient(value = "sea-system", fallbackFactory = SystemFeignClientFallBack.class)
public interface SystemFeignClient  {

    /**
     * 获取登录用户信息
     * 注：参数前面需要加注解指定参数传输方式，不然默认会把这个参数当成 RequestBody（即发送 POST 请求，body 里包含参数）
     * @param username
     * @return
     */
    @GetMapping("/sysUser/getLoginUser")
    public CommonResult<LoginUser> getLoginUser(@RequestParam String username);
    
}
