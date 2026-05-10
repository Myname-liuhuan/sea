package com.example.sea.common.core.handler;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 全局异常处理类
 * 这里@Order注解的值越越小越优先，GlobalExceptionHandler这里设置最大（最晚）用作兜底
 * 异常处理层级（从具体到通用）：
 * 1. 业务异常（自定义）→ 对应业务错误码，返回200/4xx
 * 2. 参数校验异常 → 400，明确提示哪个参数有问题
 * 3. 资源不存在异常（NoResourceFound等）→ 404，WARN日志
 * 4. 权限异常 → 403，记录安全日志
 * 5. 系统异常（数据库、RPC等）→ 500，ERROR日志+告警+错误ID
 * 6. 兜底异常（Exception.class）→ 500，ERROR日志+P0告警
 * @description 全局异常处理类
 * @author liuhuan
 * @date 2025-04-01
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验@Validated异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletResponse response) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMsg.append(fieldError.getDefaultMessage()).append(";");
            }
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return CommonResult.validateFailed(errorMsg.toString());
    }

    /**
     * 处理自定义业务异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<String> handleBusinessException(BusinessException ex, HttpServletResponse response) {
        if (ex.getErrorCode() != null) {
            response.setStatus((int) ex.getErrorCode().getCode());
            return CommonResult.failed(ex.getErrorCode(), ex.getMessage());
        }
        return CommonResult.failed(ex.getMessage());
    }

    /**
     * 处理404等状态异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<String> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        logger.warn("Request path not found - Path: {}, Method: {}", request.getRequestURI(), request.getMethod());
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return CommonResult.notFound("请求路径不存在");
    }

    /**
     * 处理静态资源未找到异常（Spring Boot 3.x）
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public CommonResult<String> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        logger.warn("Resource not found - Path: {}, Method: {}", request.getRequestURI(), request.getMethod());
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return CommonResult.notFound("请求资源不存在");
    }

    // 处理请求体缺失异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<String> handleMissingRequestBody(HttpMessageNotReadableException ex, HttpServletResponse response) {
        String message = "请求体缺失，请确认 POST 请求是否带上 JSON 或者请求参数";
        logger.warn(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return CommonResult.validateFailed(message);
    }

    // 处理参数缺失
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<String> handleMissingRequestParam(MissingServletRequestParameterException ex, HttpServletResponse response) {
        String message = "请求参数缺失: " + ex.getParameterName();
        logger.warn(message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理Content-Type不支持异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = String.format("Content-Type '%s' 不被支持，请使用支持的Content-Type: %s",
            ex.getContentType(), ex.getSupportedMediaTypes().stream().map(MediaType::toString).collect(Collectors.joining("或")));
        logger.warn("Content-Type not supported - Path: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理Multipart请求异常（如：未使用multipart/form-data上传文件）
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(MultipartException.class)
    public CommonResult<String> handleMultipartException(MultipartException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "Current request is not a multipart request";
        logger.warn("Multipart request error - Path: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理其他所有异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logger.error("Unexpected error occurred - Path: {}, Method: {}, Error: {}",
            request.getRequestURI(), request.getMethod(), ex.getMessage(), ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return CommonResult.failed("系统内部错误");
    }
}
