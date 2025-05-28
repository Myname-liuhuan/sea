package com.example.sea.common.exception;

import com.example.sea.common.result.IErrorCode;

/**
 * 自定义业务异常类，
 * 用于抛出业务逻辑中的异常情况
 * 结合全局异常handler可以将报错内容格式化返回
 * @author liuhuan
 * @date 2025-05-28
 * @description 业务异常类
 */
public class BusinessException extends RuntimeException {
    private IErrorCode errorCode;

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
