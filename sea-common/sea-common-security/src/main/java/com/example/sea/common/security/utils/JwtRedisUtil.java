package com.example.sea.common.security.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

/**
 * JWT Redis 存储工具类
 * 用于将JWT token存储到Redis中，支持token的存储、验证和删除
 * @author liuhuan
 * @date 2025-08-20
 */
@Component
public class JwtRedisUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Redis中JWT token的key前缀
     */
    private static final String JWT_TOKEN_KEY_PREFIX = "jwt:token:";
    
    /**
     * Redis中用户token的key前缀
     */
    private static final String USER_TOKEN_KEY_PREFIX = "jwt:user:";

    /**
     * Redis中用户token版本号的key前缀 (用于实现用户踢下线所有设备)
     */
    private static final String JWT_TOKEN_VERSION_KEY_PREFIX = "jwt:token:version:";

    /** 
     * Redis中JWT token黑名单的key前缀 (用于实现token注销)
     */
    private static final String JWT_TOKEN_BLACKLIST_KEY_PREFIX = "jwt:token:blacklist:";




    /**
     * 存储JWT token到Redis
     * @param token JWT token
     * @param userId 用户ID
     * @param expiration 过期时间（毫秒）
     */
    public void storeToken(String token, Long userId, long expiration) {
        String tokenId = extractTokenId(token);
        String tokenKey = JWT_TOKEN_KEY_PREFIX + tokenId;
        String userKey = USER_TOKEN_KEY_PREFIX + userId;
        
        // 存储token到token key
        redisTemplate.opsForValue().set(tokenKey, token, expiration, TimeUnit.MILLISECONDS);
        //清理
        this.cleanupExpiredTokenIds(userKey);
        // 存储token ID到用户 key，用于管理用户的所有token
        redisTemplate.opsForSet().add(userKey, tokenId);
        redisTemplate.expire(userKey, expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 清理userKey集合中 过期的tokenid
     * @param userKey
     */
    private void cleanupExpiredTokenIds(String userKey) {
        var tokenIds = redisTemplate.opsForSet().members(userKey);
        if (tokenIds != null) {
            for (String tokenId : tokenIds) {
                String tokenKey = JWT_TOKEN_KEY_PREFIX + tokenId;
                if (!Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
                    redisTemplate.opsForSet().remove(userKey, tokenId);
                }
            }
        }
    }

    /**
     * 验证token是否存在于Redis中
     * @param token JWT token
     * @return 是否存在
     */
    public boolean validateToken(String token) {
        try {
            String tokenId = extractTokenId(token);
            String tokenKey = JWT_TOKEN_KEY_PREFIX + tokenId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Redis中删除token
     * @param token JWT token
     */
    public void removeToken(String token) {
        String tokenId = extractTokenId(token);
        Long userId = extractUserId(token);
        
        String tokenKey = JWT_TOKEN_KEY_PREFIX + tokenId;
        String userKey = USER_TOKEN_KEY_PREFIX + userId;
        
        // 删除token
        redisTemplate.delete(tokenKey);
        
        // 从用户的token集合中移除
        redisTemplate.opsForSet().remove(userKey, tokenId);
    }

    /**
     * 删除用户的所有token（用户登出所有设备）
     * @param userId 用户ID
     */
    public void removeAllUserTokens(Long userId) {
        String userKey = USER_TOKEN_KEY_PREFIX + userId;
        
        // 获取用户的所有token ID
        var tokenIds = redisTemplate.opsForSet().members(userKey);
        if (tokenIds != null) {
            // 删除所有token
            for (String tokenId : tokenIds) {
                String tokenKey = JWT_TOKEN_KEY_PREFIX + tokenId;
                redisTemplate.delete(tokenKey);
            }
        }
        
        // 删除用户的token集合
        redisTemplate.delete(userKey);
    }

    /**
     * 获取用户的token数量
     * @param userId 用户ID
     * @return token数量
     */
    public long getUserTokenCount(Long userId) {
        String userKey = USER_TOKEN_KEY_PREFIX + userId;
        Long count = redisTemplate.opsForSet().size(userKey);
        return count != null ? count : 0;
    }

    /**
     * 从token中提取token ID (jti)
     * @param token JWT token
     * @return token ID
     */
    private String extractTokenId(String token) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            return claims.getId();
        } catch (Exception e) {
            throw new RuntimeException("无法从token中提取token ID", e);
        }
    }

    /**
     * 从token中提取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    private Long extractUserId(String token) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("无法从token中提取用户ID", e);
        }
    }

    /**
     * 获取用户当前token的版本号
     * @param userId
     * @return
     */
    public Long getUserVersion(String userId){
        String versionKey = JWT_TOKEN_VERSION_KEY_PREFIX + userId;
        String versionStr = redisTemplate.opsForValue().get(versionKey);
        return versionStr == null ? 0L : Long.parseLong(versionStr);
    }

    /**
     * 用户的token版本号+1 (踢下线所有token)
     * @param userId
     * @return
     */
     public long incrTokenVersion(Long userId) {
        String key = JWT_TOKEN_VERSION_KEY_PREFIX + userId;
        Long v = redisTemplate.opsForValue().increment(key);
        return v == null ? 0L : v;
    }

    /**
     * 检查token是否在黑名单中
     * @param jti
     * @return
     */
    public boolean isBlacklisted(String jti) {
        String key = JWT_TOKEN_BLACKLIST_KEY_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 将token加入黑名单(踢掉单个token)
     * @param jti
     * @param ttlSec
     */
    public void addToBlacklist(String jti, long ttlSec) {
        String key = JWT_TOKEN_BLACKLIST_KEY_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "1", ttlSec, TimeUnit.SECONDS);
    }

}
