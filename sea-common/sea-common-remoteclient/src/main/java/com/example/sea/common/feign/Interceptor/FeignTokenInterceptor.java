package com.example.sea.common.feign.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.sea.common.security.properties.SecurityWhiteListProperties;
import com.example.sea.common.security.utils.JwtUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign 请求拦截器，用于传递和生成 JWT Token
 * @author liuhuan
 * @date 2025-08-25
 */
@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    private final SecurityWhiteListProperties securityProperties;
    private final JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Autowired
    public FeignTokenInterceptor(SecurityWhiteListProperties securityProperties, JwtUtil jwtUtil) {
        this.securityProperties = securityProperties;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 给feign请求添加token通过security验证
     * 有token就直接透传,没有但是在白名单中就生成白名单token
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        // 透传 token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token)) {
            template.header(HttpHeaders.AUTHORIZATION, token);
        } else {
            String path = request.getRequestURI();
            // 在白名单中生成认证token
            for (String pattern : securityProperties.getWhitelist()) {
                if (pathMatcher.match(pattern, path)) {
                    String whiteToken = jwtUtil.generateWhiteToken();
                    template.header(HttpHeaders.AUTHORIZATION, "Bearer " + whiteToken);
                    return;
                }
            }
        }
    }

}
