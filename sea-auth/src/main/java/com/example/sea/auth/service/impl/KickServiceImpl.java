package com.example.sea.auth.service.impl;

import org.springframework.stereotype.Service;

import com.example.sea.auth.service.KickService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.JwtRedisUtil;
import com.example.sea.common.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 在线踢人服务
 * @author liuhuan
 * @date 2025-08-26 
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KickServiceImpl implements KickService {

    private final JwtUtil jwtUtil;
    private final JwtRedisUtil jwtRedisUtil;

    @Override
    public CommonResult<Boolean> kickByAccessToken(String token) {
       try{
            Claims claim = jwtUtil.parseToken(token);
            long second = jwtUtil.remainSeconds(claim);
            jwtRedisUtil.addToBlacklist(claim.getId(), second);
            return CommonResult.success(true);
       } catch(Exception e){
            log.error("踢token下线错误：{}", e.getMessage(), e);
            return CommonResult.failed("操作失败");
       }
    }

    @Override
    public CommonResult<Boolean> kickUserAll(Long userId) {
        long newVersion = jwtRedisUtil.incrTokenVersion(userId);
        jwtRedisUtil.removeAllUserTokens(userId);
        return CommonResult.success(newVersion > 0);
    }
    
}
