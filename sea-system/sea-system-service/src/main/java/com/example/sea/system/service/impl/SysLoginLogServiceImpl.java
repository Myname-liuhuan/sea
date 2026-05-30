package com.example.sea.system.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.system.api.dto.LoginLogDTO;
import com.example.sea.system.dao.SysLoginLogMapper;
import com.example.sea.system.entity.SysLoginLogPO;
import com.example.sea.system.service.ISysLoginLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录日志服务实现类
 * @author liuhuan
 * @date 2026-05-18
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLogPO> implements ISysLoginLogService {

    /**
     * 记录登录日志
     * @param loginLogDTO 登录日志DTO
     */
    public void recordLoginLog(LoginLogDTO loginLogDTO) {
        SysLoginLogPO loginLogPO = new SysLoginLogPO();
        loginLogPO.setUserId(loginLogDTO.getUserId());
        loginLogPO.setUsername(loginLogDTO.getUsername());
        loginLogPO.setIpAddress(loginLogDTO.getIpAddress());
        loginLogPO.setLoginLocation(loginLogDTO.getLoginLocation());
        loginLogPO.setBrowser(loginLogDTO.getBrowser());
        loginLogPO.setOs(loginLogDTO.getOs());
        loginLogPO.setStatus(loginLogDTO.getStatus());
        loginLogPO.setMsg(loginLogDTO.getMsg());
        loginLogPO.setFailReason(loginLogDTO.getFailReason());
        loginLogPO.setLoginTime(loginLogDTO.getLoginTime());
        this.save(loginLogPO);
    }
}