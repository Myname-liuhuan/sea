package com.example.sea.system.service.impl;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.converter.SysUserConverter;
import com.example.sea.system.dao.SysUserMapper;
import com.example.sea.system.entity.SysUser;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.interfaces.dto.SysUserQueryDTO;
import com.example.sea.system.interfaces.vo.SysUserVO;
import com.example.sea.system.service.ISysUsersService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 用户表服务实现类
 * @author liuhuan
 * @date 2025-05-28
 */
@Service
public class SysUsersServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUsersService {

    private final SysUserConverter sysUserConverter;

    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();

    @Autowired
    public SysUsersServiceImpl(SysUserConverter sysUserConverter) {
        this.sysUserConverter = sysUserConverter;
    }

    /**
     * 新增用户信息
     */
    @Override
    public CommonResult<Boolean> save(SysUserDTO sysUserDTO) {
        SysUser entity = sysUserConverter.dtoToEntity(sysUserDTO);
        //BCrypt加密密码
        entity.setPasswordHash(bCryptPasswordEncoder.encode(sysUserDTO.getPassword()));
        boolean result = this.save(entity);
        this.updateById(entity);
        return CommonResult.success(result);
    }

    /**
     * 更新用户信息
     */
    @Override
    public CommonResult<Boolean> update(SysUserDTO sysUserDTO) {
        SysUser entity = sysUserConverter.dtoToEntity(sysUserDTO);
        //BCrypt加密密码
        if (Objects.nonNull(sysUserDTO.getPassword())) {
            entity.setPasswordHash(bCryptPasswordEncoder.encode(sysUserDTO.getPassword()));
        }
        boolean result = this.updateById(entity);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<List<SysUserVO>> list(SysUserQueryDTO sysUserQueryDTO) {
    
        List<SysUserVO> userList = this.baseMapper.list(sysUserQueryDTO);
        return CommonResult.success(userList);

    }
    
    /**
     * 校验登录用户信息
     * @param username 用户名
     * @return 返回登录用户信息
     */
    @Override
    public CommonResult<LoginUser> checkLoginUser(String username) {
        //lambdaquery通过用户名查询用户
        List<SysUser> userList = this.baseMapper.selectList(
            Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username)
        );

        if(!CollectionUtils.isEmpty(userList)){
            SysUser sysUser = userList.get(0);
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(sysUser, loginUser);
            return CommonResult.success(loginUser);
        }
        return CommonResult.failed();
    }

    


}
