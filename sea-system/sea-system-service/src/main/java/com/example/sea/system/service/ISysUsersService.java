package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.entity.SysUser;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.interfaces.dto.SysUserQueryDTO;
import com.example.sea.system.interfaces.vo.SysUserVO;

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

    /**
     * 查询用户列表
     * @param sysUserQueryDTO 查询参数
     * @return 用户列表
     */
    CommonResult<List<SysUserVO>> list(SysUserQueryDTO sysUserQueryDTO);

}
