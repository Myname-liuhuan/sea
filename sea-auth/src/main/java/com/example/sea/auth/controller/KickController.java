package com.example.sea.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.auth.service.KickService;
import com.example.sea.common.core.result.CommonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/kick")
@Tag(name = "踢下线管理", description = "用户踢下线相关操作接口")
@RequiredArgsConstructor
public class KickController {

    private final KickService kickService;

    /**
     * 指定token踢下线
     * @param token
     * @return
     */
    @GetMapping("/kickToken")
    @Operation(summary = "指定token踢下线", description = "根据指定的accessToken将用户踢下线")
    public CommonResult<Boolean> kickByAccessToken(String token){
        return kickService.kickByAccessToken(token);
    }

    /**
     * 指定用户踢下线(该用户的所有token)
     * @param userId
     * @return
     */
    @GetMapping("/kickUserAll")
    @Operation(summary = "踢用户所有设备下线", description = "根据用户ID将该用户的所有token踢下线")
    public CommonResult<Boolean> kickUserAll(Long userId){
        return kickService.kickUserAll(userId);
    }

    
}
