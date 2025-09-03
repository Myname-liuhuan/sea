package com.example.sea.common.core.handler;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理类
 * @description 全局异常处理类
 * @author liuhuan
 * @date 2025-04-01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验@Validated异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMsg.append(fieldError.getDefaultMessage()).append(";");
            }
        }
        return CommonResult.validateFailed(errorMsg.toString());
    }

    /**
     * 处理自定义业务异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<String> handleBusinessException(BusinessException ex) {
        return CommonResult.failed(ex.getMessage());
    }

    /**
     * 处理404等状态异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<String> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("Request path not found - Path: {}, Method: {}", request.getRequestURI(), request.getMethod());
        return CommonResult.failed("请求路径不存在");
    }

    // 处理请求体缺失异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<String> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        String message = "请求体缺失，请确认 POST 请求是否带上 JSON 或者请求参数";
        logger.warn(message);
        return CommonResult.failed(message);
    }

    // 处理参数缺失
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<String> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        String message = "请求参数缺失: " + ex.getParameterName();
        logger.warn(message);
        return CommonResult.failed(message);
    }

    /**
     * 处理Content-Type不支持异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("Content-Type '%s' 不被支持，请使用支持的Content-Type: %s", 
            ex.getContentType(), ex.getSupportedMediaTypes().stream().map(MediaType::toString).collect(Collectors.joining("或")));
        logger.warn("Content-Type not supported - Path: {}, Method: {}, Error: {}", 
            request.getRequestURI(), request.getMethod(), message);
        return CommonResult.failed(message);
    }

    /**
     * 处理其他所有异常
     * @param ex 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred - Path: {}, Method: {}, Error: {}", 
            request.getRequestURI(), request.getMethod(), ex.getMessage(), ex);
        return CommonResult.failed("系统内部错误");
    }
}
