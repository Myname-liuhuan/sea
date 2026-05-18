package com.example.sea.system.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 操作日志表实体类
 * @author liuhuan
 * @date 2026-05-18
 */
@Data
@Accessors(chain = true)
@TableName("sys_operation_log")
public class SysOperationLogPO {

    /**
     * 日志ID（雪花算法）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 操作标题
     */
    private String title;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类型（0其它，1后台用户，2手机端用户）
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
     * 请求URL
     */
    private String operationUrl;

    /**
     * 操作IP地址
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
     * 操作状态（0异常，1正常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 耗时(ms)
     */
    private Integer duration;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

}
