package com.example.sea.system.api.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 操作日志数据传输对象
 * @author liuhuan
 * @date 2026-05-18
 */
@Schema(description = "操作日志DTO")
@Data
@Accessors(chain = true)
public class OperationLogDTO {

    /**
     * 操作标题
     */
    private String title;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类型
     */
    private Integer operatorType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作地址
     */
    private String operationUrl;

    /**
     * 操作IP
     */
    private String operationIp;

    /**
     * 操作地点
     */
    private String operationLocation;

    /**
     * 请求参数
     */
    private String operationParam;

    /**
     * 返回参数
     */
    private String responseParam;

    /**
     * 操作状态（0=异常，1=正常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时长（毫秒）
     */
    private Integer duration;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
}
