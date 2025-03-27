package com.example.sea.common.result;

/**
 * API返回码封装类
 * @author liuhuan
 * @date 2025-03-27
 */
public interface IErrorCode {
    
    /**
     * 返回码
     * @return 返回码
     */
    long getCode();

    /**
     * 返回信息
     * @return 返回信息
     */
    String getMessage();
}
