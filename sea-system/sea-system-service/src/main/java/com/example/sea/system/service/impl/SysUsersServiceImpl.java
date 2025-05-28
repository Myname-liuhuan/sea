package com.example.sea.system.service.impl;

import com.example.sea.common.result.CommonResult;
import com.example.sea.system.converter.SysUserConverter;
import com.example.sea.system.dao.SysUsersMapper;
import com.example.sea.system.entity.SysUser;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.service.ISysUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户表服务实现类
 * @author liuhuan
 * @date 2025-05-28
 */
@Service
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUser> implements ISysUsersService {

    private final SysUserConverter sysUserConverter;

    @Autowired
    public SysUsersServiceImpl(SysUserConverter sysUserConverter) {
        this.sysUserConverter = sysUserConverter;
    }

    @Override
    public CommonResult<Boolean> save(SysUserDTO sysUser) {
        SysUser entity = sysUserConverter.dtoToEntity(sysUser);
        boolean result = this.save(entity);
        return CommonResult.success(result);
    }


}
