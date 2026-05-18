package com.example.sea.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.system.entity.SysOperationLogPO;

/**
 * 操作日志表服务接口
 * @author liuhuan
 * @date 2026-05-18
 */
public interface ISysOperationLogService extends IService<SysOperationLogPO> {

    /**
     * 保存操作日志
     * @param operationLog 操作日志实体
     * @return 
     */
    boolean save(SysOperationLogPO operationLog);

}
