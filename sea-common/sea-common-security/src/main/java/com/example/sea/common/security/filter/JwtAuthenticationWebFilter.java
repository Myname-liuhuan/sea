package com.example.sea.common.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.sea.common.security.constants.SecurityConstants;
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

        // 白名单直接放行  (注:放行逻辑只能在解析token前,解析token后SpringSecurity会要求必须给SecurityContext值)
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

        JwtUtil.TokenParseResult tokenParseResult = jwtUtil.parseTokenWithExpirationStatus(token);
        if (tokenParseResult.isExpired()) {
            unauthorized(response, "token已过期");
            return;
        }

        if (!tokenParseResult.isSuccess()) {
            unauthorized(response, "无效的token");
            return;
        }

        Claims claims = tokenParseResult.getClaims();
        //判断是否是服务间通信,不是就需要进一步校验
        if (!Objects.equals(SecurityConstants.INTERNAL_FEIGN, claims.getId())) {
            //只允许accessToken
            String tokenType = claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class);
            if(!Objects.equals(SecurityConstants.TOKEN_TYPE_ACCESS, tokenType)){
                log.error("只允许accessToken访问接口,token:{}", token);
                unauthorized(response, "无效的token");
                return;
            }
            //验证token是否在黑名单
            if(jwtRedisUtil.isBlacklisted(claims.getId())){
                log.error("黑名单token:{}", token);
                unauthorized(response, "无效的token");
                return;
            }
            //验证版本号
            Long version = jwtRedisUtil.getUserVersion(claims.getSubject());
            Long tokenVersion = claims.get(SecurityConstants.CLAIM_VERSION, Long.class);
            if(version != tokenVersion){
                log.error("旧版本token:{}", token);
                unauthorized(response, "无效的token");
                return;
            }
        }

        // 将用户信息存储到security context中
        LoginUser loginUser = new LoginUser();
        loginUser.setId(Long.parseLong(claims.getSubject()));
        loginUser.setUsername(claims.get(SecurityConstants.CLAIM_USERNAME, String.class));
        loginUser.setRoles(claims.get(SecurityConstants.CLAIM_ROLES, List.class));
        loginUser.setPerms(claims.get(SecurityConstants.CLAIM_AUTHS, List.class));

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
