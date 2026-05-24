package com.example.sea.common.mybatis.aspect;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.sea.common.mybatis.annotation.OperationLog;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.log.api.dto.OperationLogDTO;
import com.example.sea.log.api.feign.OperationLogFeignClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志切面
 * 拦截带有@OperationLog注解的方法，记录操作日志
 * @author liuhuan
 * @date 2026-05-18
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogFeignClient operationLogFeignClient;
    private final ThreadPoolTaskExecutor operationLogExecutor;

    /**
     * 敏感字段正则表达式
     */
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            "(\"(password|token|secret|accessKey|accessKeySecret|cardNumber|cvv|phone|mobile|email)\"[\\s:]*[\"]?)([^\"]*)([\"]?)",
            Pattern.CASE_INSENSITIVE);

    /**
     * 定义切点：带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.example.sea.common.mybatis.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：拦截操作并记录日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        HttpServletRequest request = getRequest();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = signature.getMethod().getAnnotation(OperationLog.class);

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 获取当前用户
        LoginUser loginUser = SecurityContextUtil.getLoginUser();

        OperationLogDTO logDTO = new OperationLogDTO();
        logDTO.setTitle(operationLog.title());
        logDTO.setBusinessType(operationLog.businessType());
        logDTO.setOperatorType(operationLog.operatorType());
        logDTO.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        // 设置用户信息
        if (loginUser != null) {
            logDTO.setUserId(loginUser.getId());
            logDTO.setUsername(loginUser.getUsername());
        }

        // 设置请求信息
        if (request != null) {
            logDTO.setRequestMethod(request.getMethod());
            logDTO.setOperationUrl(request.getRequestURI());
            logDTO.setOperationIp(getIpAddress(request));
            logDTO.setOperationLocation("");
            logDTO.setOperationParam(maskSensitiveData(getParamsString(request)));
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 设置响应参数
            logDTO.setResponseParam(maskSensitiveData(getResultString(result)));
            logDTO.setStatus(1);

            return result;
        } catch (Throwable e) {
            // 异常时记录错误信息
            logDTO.setStatus(0);
            logDTO.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算耗时
            long duration = System.currentTimeMillis() - startTime;
            logDTO.setDuration((int) duration);

            // 构建日志信息
            logDTO.setOperationTime(LocalDateTime.now());

            // 根据async属性决定同步或异步保存
            if (operationLog.async()) {
                // 异步保存
                operationLogExecutor.submit(() -> {
                    try {
                        operationLogFeignClient.saveOperationLog(logDTO);
                    } catch (Exception e) {
                        log.error("Async save operation log failed", e);
                    }
                });
            } else {
                // 同步保存
                operationLogFeignClient.saveOperationLog(logDTO);
            }
        }
    }

    /**
     * 获取HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取请求参数字符串
     */
    private String getParamsString(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        parameterMap.forEach((key, values) -> {
            sb.append(key).append("=");
            if (values != null && values.length > 0) {
                sb.append(String.join(",", values));
            }
            sb.append("&");
        });
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 获取结果字符串
     */
    private String getResultString(Object result) {
        if (result == null) {
            return "";
        }
        return result.toString();
    }

    /**
     * 脱敏敏感数据
     */
    private String maskSensitiveData(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        Matcher matcher = SENSITIVE_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 保留字段名，将值替换为***
            matcher.appendReplacement(sb, matcher.group(1) + "***" + (matcher.group(4) != null ? matcher.group(4) : ""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}