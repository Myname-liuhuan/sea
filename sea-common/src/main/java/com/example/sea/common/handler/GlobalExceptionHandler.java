package com.example.sea.common.handler;

import com.example.sea.common.exception.BusinessException;
import com.example.sea.common.result.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

/**
 * 代码生成全局异常处理类
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
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<CommonResult<?>>> handleValidationException(WebExchangeBindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMsg.append(fieldError.getDefaultMessage()).append(";");
            }
        }
        return Mono.just(ResponseEntity
                .badRequest()
                .body(CommonResult.validateFailed(errorMsg.toString())));
    }

    /**
     * 处理参数校验@Validated异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(ServerWebInputException.class) 
    public Mono<ResponseEntity<CommonResult<?>>> handleServerWebInputException(ServerWebInputException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(CommonResult.validateFailed(ex.getReason())));
    }

    /**
     * 处理自定义业务异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<CommonResult<String>>> handleBusinessException(BusinessException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(CommonResult.failed(ex.getMessage())));
    }

    /**
     * 处理404等状态异常
     * @param ex 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<CommonResult<String>>> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Request path not found - Status: {}, Reason: {}", ex.getStatusCode(), ex.getReason());
        return Mono.just(ResponseEntity
                .status(ex.getStatusCode())
                .body(CommonResult.failed(ex.getReason())));
    }
}
