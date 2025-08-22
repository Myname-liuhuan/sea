package com.example.sea.common.core.handler;

import com.example.sea.common.core.exception.BusinessException;
import com.example.sea.common.core.result.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

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
