package com.example.sea.auth.service;

import com.example.sea.common.core.result.CommonResult;


/**
 * 踢人下线服务
 * @author liuhuan
 * @date 2025-08-26
 */
public interface KickService {
    
    /**
     * 踢掉指定token
     * @param token
     * @return
     */
    CommonResult<Boolean> kickByAccessToken(String token);

    /**
     * 踢掉该用户下所有token
     * @param userId
     * @return
     */
    CommonResult<Boolean> kickUserAll(Long userId);

} 