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
    NOT_FOUND(404, "资源不存在"),

    // 参数相关
    PARAM_MISSING(10001, "缺少必要参数"),
    PARAM_INVALID(10002, "参数格式错误"),
    PARAM_VALUE_INVALID(10003, "参数值不合法"),

    // 数据相关
    DATA_NOT_EXIST(10004, "数据不存在"),
    DATA_ALREADY_EXIST(10005, "数据已存在"),
    DATA_EXPIRED(10006, "数据已过期"),

    // 请求相关
    OPERATION_TOO_FREQUENT(10007, "操作过于频繁"),
    REQUEST_DUPLICATE(10008, "重复请求");

    /** 返回码 */
    private final long code;
    
    /** 返回信息 */
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
