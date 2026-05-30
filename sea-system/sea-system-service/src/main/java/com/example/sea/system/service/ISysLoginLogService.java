package com.example.sea.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.system.api.dto.LoginLogDTO;
import com.example.sea.system.entity.SysLoginLogPO;

/**
 * 登录日志表服务接口
 * @author liuhuan
 * @date 2026-05-18
 */
public interface ISysLoginLogService extends IService<SysLoginLogPO> {

    /**
     * 记录登录日志
     * @param loginLogDTO 登录日志DTO
     */
    void recordLoginLog(LoginLogDTO loginLogDTO);
}