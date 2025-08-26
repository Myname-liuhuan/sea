package com.example.sea.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.auth.service.KickService;
import com.example.sea.common.core.result.CommonResult;

@RestController
@RequestMapping("/kick")
public class KickController {

    private final KickService kickService;

    public KickController(KickService kickService){
        this.kickService =  kickService;
    }

    /**
     * 指定token踢下线
     * @param token
     * @return
     */
    @GetMapping("/kickToken")
    public CommonResult<Boolean> kickByAccessToken(String token){
        return kickService.kickByAccessToken(token);
    }

    /**
     * 指定用户踢下线(该用户的所有token)
     * @param userId
     * @return
     */
    @GetMapping("/kickUserAll")
    public CommonResult<Boolean> kickUserAll(Long userId){
        return kickService.kickUserAll(userId);
    }

    
}
