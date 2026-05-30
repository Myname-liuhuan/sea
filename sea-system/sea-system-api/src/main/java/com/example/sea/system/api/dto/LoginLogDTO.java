package com.example.sea.system.api.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录日志数据传输对象
 * @author liuhuan
 * @date 2026-05-18
 */
@Schema(description = "登录日志DTO")
@Data
@Accessors(chain = true)
public class LoginLogDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态（0失败，1成功）
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
}