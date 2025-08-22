package com.example.sea.common.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.properties.SecurityWhiteListProperties;
import com.example.sea.common.security.utils.JwtRedisUtil;
import com.example.sea.common.security.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationWebFilter extends OncePerRequestFilter {

    private final SecurityWhiteListProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    private final JwtRedisUtil jwtRedisUtil;

    @Autowired
    public JwtAuthenticationWebFilter(SecurityWhiteListProperties securityProperties, JwtUtil jwtUtil, JwtRedisUtil jwtRedisUtil) {
        this.securityProperties = securityProperties;
        this.jwtUtil = jwtUtil;
        this.jwtRedisUtil = jwtRedisUtil;
    }

    /** 验证不通过的提示 */
    private static final String UNAUTHORIZED_MESSAGE_TEMPLATE = "{\"code\":401,\"message\":\"%s\"}";

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();

        // 白名单直接放行
        for (String pattern : securityProperties.getWhitelist()) {
            if (pathMatcher.match(pattern, path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = resolveToken(request);
        if (token == null) {
            unauthorized(response, "token缺失");
            return;
        }

        // 验证token是否存在于Redis中
        // if (!jwtRedisUtil.validateToken(token)) {
        //     unauthorized(response, "无效的token");
        //     return;
        // }

        JwtUtil.TokenParseResult tokenParseResult = jwtUtil.parseTokenWithExpirationStatus(token);
        if (tokenParseResult.isExpired()) {
            unauthorized(response, "token已过期");
            return;
        }

        if (!tokenParseResult.isSuccess()) {
            unauthorized(response, "无效的token");
            return;
        }

        // 将用户信息存储到security context中
        Claims claims = tokenParseResult.getClaims();
        LoginUser loginUser = new LoginUser();
        loginUser.setId(Long.parseLong(claims.getSubject()));
        loginUser.setUsername(claims.get("username", String.class));
        loginUser.setRoles(claims.get("roles", List.class));
        loginUser.setPerms(claims.get("authorities", List.class));

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(loginUser, token, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }

    /**
     * 提取token
     * @param request
     * @return
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 验证不通过时返回未授权响应 自定义提示
     * @param response
     * @param message
     * @throws IOException
     */
    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(String.format(UNAUTHORIZED_MESSAGE_TEMPLATE, message));
    }
}
