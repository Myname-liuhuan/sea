package com.example.sea.system.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.system.dao.SysOperationLogMapper;
import com.example.sea.system.entity.SysOperationLogPO;
import com.example.sea.system.service.ISysOperationLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志表服务实现类
 * @author liuhuan
 * @date 2026-05-18
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLogPO> implements ISysOperationLogService {

    @Override
    public boolean save(SysOperationLogPO operationLog) {
        return baseMapper.insert(operationLog) > 0;
    }

}
