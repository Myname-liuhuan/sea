package com.example.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.result.CommonResult;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.system.entity.SysUser;

/**
 * 用户表服务接口
 * @author liuhuan
 * @date 2025-05-28
 */
public interface ISysUsersService extends IService<SysUser> {

    /**
     * 保存用户信息
     * @param sysUser
     * @return
     */
    CommonResult<Boolean> save(SysUserDTO sysUser);

}
