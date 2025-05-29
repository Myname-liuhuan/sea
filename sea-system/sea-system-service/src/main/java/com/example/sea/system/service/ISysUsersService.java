package com.example.sea.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.result.CommonResult;
import com.example.sea.system.entity.SysUser;
import com.example.sea.system.interfaces.dto.SysUserDTO;

/**
 * 用户表服务接口
 * @author liuhuan
 * @date 2025-05-28
 */
public interface ISysUsersService extends IService<SysUser> {

    /**
     * 保存用户信息
     * @param sysUserDTO 入参
     * @return
     */
    CommonResult<Boolean> save(SysUserDTO sysUserDTO);

    /**
     * 更新用户信息
     * @param sysUserDTO 入参
     * @return 结果
     */
    CommonResult<Boolean> update(SysUserDTO sysUserDTO);

}
