package com.example.sea.common.core.result;

import lombok.Getter;

/**
 * API返回码封装类
 * @author liuhuan
 */
@Getter
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在");

    /** 返回码 */
    private final long code;
    
    /** 返回信息 */
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
