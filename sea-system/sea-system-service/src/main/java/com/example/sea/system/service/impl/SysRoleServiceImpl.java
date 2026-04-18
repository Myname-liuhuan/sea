package com.example.sea.system.service.impl;

import com.example.sea.system.service.ISysRoleService;

import lombok.RequiredArgsConstructor;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.converter.SysRoleConverter;
import com.example.sea.system.dao.SysRoleMapper;
import com.example.sea.system.entity.SysRolePO;
import com.example.sea.system.api.dto.SysRoleDTO;
import com.example.sea.system.api.dto.SysRoleMenuDTO;
import com.example.sea.system.api.dto.SysRoleUserDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 角色表服务实现类
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRolePO> implements ISysRoleService {

    private final SysRoleConverter sysRoleConverter;

    /**
     * 新增角色信息
     */
    @Override
    public CommonResult<Boolean> add(SysRoleDTO sysRoleDTO) {
        SysRolePO sysRole = sysRoleConverter.dtoToEntity(sysRoleDTO);
        boolean result = this.save(sysRole);
        return CommonResult.success(result);
    }

    /**
     * 更新角色信息
     * 更新角色用户关系
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> edit(SysRoleDTO sysRoleDTO) {
        SysRolePO sysRole = sysRoleConverter.dtoToEntity(sysRoleDTO);
        boolean result = this.updateById(sysRole);
        return CommonResult.success(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> editRoleUserRelation(SysRoleUserDTO sysRoleUserDTO) {
        Long roleId = sysRoleUserDTO.getRoleId();
        // 删除该角色下的所有用户
        baseMapper.deleteRoleUsersByRoleId(roleId);
        // 添加新的用户角色关系
        if(!CollectionUtils.isEmpty(sysRoleUserDTO.getUserIdList())){
            baseMapper.insertRoleUsers(roleId, sysRoleUserDTO.getUserIdList());
        }
        return CommonResult.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Void> editRoleMenuRelation(SysRoleMenuDTO sysMenuUserDTO) {
        Long roleId = sysMenuUserDTO.getRoleId();
        // 删除该角色下的所有菜单
        baseMapper.deleteRoleMenusByRoleId(roleId);
        // 添加新的角色菜单关系
        if(!CollectionUtils.isEmpty(sysMenuUserDTO.getMenuIdList())){
            baseMapper.insertRoleMenus(roleId, sysMenuUserDTO.getMenuIdList());
        }
        return CommonResult.success();
    }

}
