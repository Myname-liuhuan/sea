package com.example.sea.common.security.utils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.sea.common.security.constants.SecurityConstants;
import com.example.sea.common.security.entity.LoginUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * jwt解析、生成 工具类
 * @author liuhuan
 * @date 2025-07-30
 */
@Component
public class JwtUtil implements InitializingBean {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * AccessToken 过期时间
     */
    @Value("${jwt.expirationMs}")
    private long expirationMs;

    /**
     * RefreshToken 过期时间
     * 默认7天
     * 可根据实际需求调整，通常RefreshToken有效期更长
     */
    @Value("${jwt.refreshExpirationMs:604800000}")
    private long refreshExpirationMs;

    private SecretKey secretKey;

    @Override
    public void afterPropertiesSet() {
        /*
         * 如果 secret 是 Base64 编码的（推荐做法），使用下面方式：
         *   byte[] keyBytes = Decoders.BASE64.decode(secret);
         * 否则可以用：
         *   byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
         * JJWT 要求 HMAC 密钥长度 ≥ hash 输出长度（至少 256 位）否则会抛 WeakKeyException :contentReference[oaicite:1]{index=1}
         */
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        // 密钥强度由系统校验，hmacShaKeyFor 会选择合适 HMAC 算法
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 AccessToken
     */
    public String generateAccessToken(LoginUser loginUser) {
        return buildToken(loginUser, expirationMs, SecurityConstants.TOKEN_TYPE_ACCESS);
    }


    /**
     * 生成 Refresh Token：
     * @param loginUser
     * @return
     */
    public String generateRefreshToken(LoginUser loginUser) {
        return buildToken(loginUser, refreshExpirationMs, SecurityConstants.TOKEN_TYPE_REFRESH);
    }

    /**
     * 生成白名单token（无用户信息，仅标记请求来源可信）
     * @return
     */
    public String generateWhiteToken() {
        Date now = new Date();
        return Jwts.builder()
                .id(SecurityConstants.INTERNAL_FEIGN) // 唯一的 token ID
                .subject("0")
                .claim(SecurityConstants.CLAIM_ROLES, List.of("ADMIN"))
                .claim(SecurityConstants.CLAIM_AUTHS,List.of("*:*:*"))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey)
                .compact();
    }


    /**
     * 解析并验证 JWT：
     * 报错就表示token无效
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 解析token并返回详细的解析结果，包括是否过期
     * @param token JWT token
     * @return TokenParseResult 包含解析状态和结果
     */
    public TokenParseResult parseTokenWithExpirationStatus(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return TokenParseResult.success(claims);
        } catch (ExpiredJwtException e) {
            // token已过期，但claims仍然可以获取
            return TokenParseResult.expired("Token已过期");
        } catch (Exception e) {
            // 其他异常（签名无效、格式错误等）
            return TokenParseResult.error("Token无效: " + e.getMessage());
        }
    }

    /**
     * 判断是否是 RefreshToken
     */
    public boolean isRefreshToken(String token) {
        try {
            return "REFRESH".equals(parseToken(token).get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是 AccessToken
     */
    public boolean isAccessToken(String token) {
        try {
            return "ACCESS".equals(parseToken(token).get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

     /**
      * 标准的jwt payload:
      * {
      *  "sub": "1234567890",      // 用户 ID
      *  "username": "alice",      
      *  "roles": ["USER"],        
      *  "iat": 1713500000,        // 签发时间
      *  "exp": 1713503600,        // 过期时间
      *  "jti": "9f8c7d6a-1234-4bcd-9e1a-56789abcdeff"
      * }
      */
    private String buildToken(LoginUser loginUser, long ttlMillis, String tokenType) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // 唯一的 token ID
                .subject(String.valueOf(loginUser.getId()))
                .claim(SecurityConstants.CLAIM_USERNAME, loginUser.getUsername())
                .claim(SecurityConstants.CLAIM_ROLES, loginUser.getRoles())
                .claim(SecurityConstants.CLAIM_AUTHS, loginUser.getPerms())
                .claim(SecurityConstants.CLAIM_TOKEN_TYPE, tokenType) // 标记 token 类型
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMillis))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 获取 AccessToken 过期时间
     * @return
     */
    public long getAccessTokenExpirationMs(){
        return expirationMs;
    }

    /**
     * 获取 AccessToken 过期时间
     * @return
     */
    public long getRefreshTokenExpirationMs(){
        return refreshExpirationMs;
    }

    /**
     * Token解析结果类，包含解析状态和结果
     */
    public static class TokenParseResult {
        private final boolean success;
        private final Claims claims;
        private final boolean expired;
        private final String errorMessage;

        private TokenParseResult(boolean success, Claims claims, boolean expired, String errorMessage) {
            this.success = success;
            this.claims = claims;
            this.expired = expired;
            this.errorMessage = errorMessage;
        }

        public static TokenParseResult success(Claims claims) {
            return new TokenParseResult(true, claims, false, null);
        }

        public static TokenParseResult expired(String errorMessage) {
            return new TokenParseResult(false, null, true, errorMessage);
        }

        public static TokenParseResult error(String errorMessage) {
            return new TokenParseResult(false, null, false, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public Claims getClaims() {
            return claims;
        }

        public boolean isExpired() {
            return expired;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
