package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.entity.SysUserPO;
import com.example.sea.system.api.dto.SysUserDTO;
import com.example.sea.system.api.param.SysUserQueryParam;
import com.example.sea.system.api.vo.SysUserVO;

/**
 * 用户表服务接口
 * @author liuhuan
 * @date 2025-05-28
 */
public interface ISysUserService extends IService<SysUserPO> {

    /**
     * 保存用户信息
     * @param sysUserDTO 入参
     * @return
     */
    CommonResult<Boolean> add(SysUserDTO sysUserDTO);

    /**
     * 更新用户信息
     * @param sysUserDTO 入参
     * @return 结果
     */
    CommonResult<Boolean> update(SysUserDTO sysUserDTO);

    /**
     * 查询用户列表
     * @param sysUserQueryParam 查询参数
     * @return 用户列表
     */
    CommonResult<List<SysUserVO>> list(SysUserQueryParam sysUserQueryParam);

    /**
     * 分页查询用户列表
     * @param sysUserQueryParam 查询参数
     * @return 分页结果
     */
    CommonResult<PageResult<SysUserVO>> page(SysUserQueryParam sysUserQueryParam);

    /**
     * 校验登录用户信息
     * @param username 用户名
     * @return 登录用户信息
     */
    CommonResult<LoginUser> getLoginUser(String username);


}
