package com.example.sea.code.exception;

import com.example.sea.common.result.CommonResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
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

    @ExceptionHandler(ServerWebInputException.class) 
    public Mono<ResponseEntity<CommonResult<?>>> handleServerWebInputException(ServerWebInputException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(CommonResult.validateFailed(ex.getReason())));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<CommonResult<String>>> handleBusinessException(BusinessException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(CommonResult.failed(ex.getMessage())));
    }
}
