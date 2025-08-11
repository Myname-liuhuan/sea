package com.example.sea.gateway.utils;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

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
     * 生成 JWT：
     * - sub = username
     * - iat = now
     * - exp = now + expirationMs
     * - roles （自定义 claim）
     * 使用 signWith(secretKey) 时，算法会自动与 key 匹配，不再需要显式指定 HS256 :contentReference[oaicite:2]{index=2}
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并验证 JWT：
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}


