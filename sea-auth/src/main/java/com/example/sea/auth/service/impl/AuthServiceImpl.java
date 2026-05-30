package com.example.sea.auth.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.sea.system.api.feign.LoginLogFeignClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.sea.auth.dto.LoginRequestDTO;
import com.example.sea.auth.dto.LoginResponse;
import com.example.sea.system.api.feign.SystemFeignClient;
import com.example.sea.auth.service.AuthService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.JwtRedisUtil;
import com.example.sea.common.security.utils.JwtUtil;
import com.example.sea.system.api.dto.LoginLogDTO;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务实现
 * @author liuhuan
 * @date 2025-08-04
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final JwtRedisUtil jwtRedisUtil;
    private final SystemFeignClient systemFeignClient;
    private final LoginLogFeignClient loginLogFeignClient;

    private static final String LOGIN_FAILURE_MSG = "用户名或密码错误";

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    /**
     * 用户登录，生成 JWT 令牌
     * @param username 用户名
     * @param password 密码
     * @return 登录响应，包含accessToken和refreshToken
     */
    @Override
    public CommonResult<LoginResponse> authenticate(String username, String password) {
        CommonResult<LoginUser> vaResult = this.validateUser(username, password);
        if (!vaResult.isSuccess()) {
            log.error("用户登录失败:用户名称{},失败原因:{}",username, vaResult.getMessage());
            // 记录登录失败日志
            recordLoginLog(username, null, 0, vaResult.getMessage());
            return CommonResult.failed(LOGIN_FAILURE_MSG);
        }

        LoginUser loginUser = vaResult.getData();
        //设置当前token的版本
        loginUser.setVersion(jwtRedisUtil.getUserVersion(String.valueOf(loginUser.getId())));
        //生成token
        String accessToken = jwtUtil.generateAccessToken(loginUser);
        String refreshToken = jwtUtil.generateRefreshToken(loginUser);
        Long expiresIn = jwtUtil.getAccessTokenExpirationMs();

        // 存储token到Redis
        jwtRedisUtil.storeToken(refreshToken, loginUser.getId(), jwtUtil.getRefreshTokenExpirationMs());

        // 记录登录成功日志
        recordLoginLog(username, loginUser.getId(), 1, "登录成功");

        return CommonResult.success(new LoginResponse(accessToken, refreshToken, expiresIn));
    }

    /**
     * 通过 refreshToken 刷新 AccessToken
     * @param loginRequest 刷新令牌
     * @return 新的访问令牌AccessToken
     */
    @Override
    public CommonResult<LoginResponse> refreshToken(LoginRequestDTO loginRequest) {
        String refreshToken = loginRequest.getRefreshToken();
        // 先验证token是否存在于Redis中
        if (!jwtRedisUtil.validateToken(refreshToken)) {
            return CommonResult.failed("刷新token无效或已过期");
        }

        // 先判断是不是refreshToken
        if(!jwtUtil.isRefreshToken(refreshToken)){
            return CommonResult.failed("无效的刷新token");
        }

        // 解析刷新token
        Claims claims =  jwtUtil.parseToken(refreshToken);
        String username = (String) claims.get("username");

        CommonResult<LoginUser> remoteResult = systemFeignClient.getLoginUser(username);
        if (!remoteResult.isSuccess()) {
            log.error("刷新token失败，token:{} 原因：{}", refreshToken, "用户不存在");
            return CommonResult.failed("刷新token失败,用户不存在");
        }
        //设置当前token的版本
        LoginUser loginUser = remoteResult.getData();
        loginUser.setVersion(jwtRedisUtil.getUserVersion(String.valueOf(loginUser.getId())));
        // 生成新的accessToken
        String newAccessToken = jwtUtil.generateAccessToken(loginUser);
        return CommonResult.success(new LoginResponse(newAccessToken, refreshToken, jwtUtil.getAccessTokenExpirationMs()));
    }

    /**
     * 从远程系统服务验证用户
     * @param username
     * @param password
     * @return 成功返回登录用户信息，失败返回包含错误信息的CommonResult
     */
    private CommonResult<LoginUser> validateUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return CommonResult.failed("用户名或密码不能为空");
        }

        CommonResult<LoginUser> remoteResult = systemFeignClient.getLoginUser(username);
        if (Objects.isNull(remoteResult) || !remoteResult.isSuccess()) {
            String msg = Objects.nonNull(remoteResult) ? remoteResult.getMessage() : "未知错误";
            log.error("获取用户【{}】信息失败，错误 {}", username, msg);
            return CommonResult.failed(msg);
        }

        if(StringUtils.isBlank(remoteResult.getData().getPassword())
                || !bCryptPasswordEncoder.matches(password, remoteResult.getData().getPassword())){
            return CommonResult.failed("用户名或密码错误");
        }
        return remoteResult;
    }

    /**
     * 记录登录日志
     * @param username 用户名
     * @param userId 用户ID（登录失败时为null）
     * @param status 登录状态（0失败，1成功）
     * @param msg 提示消息
     */
    private void recordLoginLog(String username, Long userId, int status, String msg) {
        try {
            HttpServletRequest request = getHttpServletRequest();
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            LoginLogDTO loginLogDTO = new LoginLogDTO();
            loginLogDTO.setUsername(username);
            loginLogDTO.setUserId(userId);
            loginLogDTO.setIpAddress(ipAddress);
            loginLogDTO.setStatus(status);
            loginLogDTO.setMsg(msg);
            loginLogDTO.setLoginTime(LocalDateTime.now());

            // 解析User-Agent获取浏览器和操作系统信息
            if (StringUtils.isNotBlank(userAgent)) {
                loginLogDTO.setBrowser(parseBrowser(userAgent));
                loginLogDTO.setOs(parseOs(userAgent));
            }

            // 如果是失败登录，设置失败原因
            if (status == 0) {
                loginLogDTO.setFailReason(msg);
            }

            loginLogFeignClient.recordLoginLog(loginLogDTO);
        } catch (Exception e) {
            log.error("记录登录日志异常: {}", e.getMessage());
            // 不影响主流程
        }
    }

    /**
     * 获取HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    /**
     * 获取客户端真实IP地址
     * 支持代理转发场景（X-Forwarded-For）
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 优先从X-Forwarded-For获取（反向代理场景）
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            // 如果有多个IP，取第一个（最原始的客户端IP）
            return xForwardedFor.split(",")[0].trim();
        }

        // 尝试X-Real-IP
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp)) {
            return xRealIp;
        }

        // 直接获取远程地址
        return request.getRemoteAddr();
    }

    /**
     * 从User-Agent解析浏览器
     */
    private String parseBrowser(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "unknown";
        }
        if (userAgent.contains("Edg")) {
            return "Microsoft Edge";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }
        if (userAgent.contains("Safari")) {
            return "Safari";
        }
        if (userAgent.contains("IE") || userAgent.contains("Trident")) {
            return "IE";
        }
        return "unknown";
    }

    /**
     * 从User-Agent解析操作系统
     */
    private String parseOs(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "unknown";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        }
        if (userAgent.contains("Mac OS")) {
            return "macOS";
        }
        if (userAgent.contains("Linux")) {
            return "Linux";
        }
        if (userAgent.contains("Android")) {
            return "Android";
        }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "unknown";
    }
}