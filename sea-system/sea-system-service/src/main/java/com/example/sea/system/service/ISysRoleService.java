package com.example.sea.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.entity.SysRole;
import com.example.sea.system.interfaces.dto.SysRoleDTO;
import com.example.sea.system.interfaces.dto.SysRoleMenuDTO;
import com.example.sea.system.interfaces.dto.SysRoleUserDTO;

/**
 * 角色表服务接口
 * @author admin
 * @date 2025-08-14
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 新增角色
     * @param sysRoleDTO
     * @return
     */
    CommonResult<Boolean> add(SysRoleDTO sysRoleDTO);

    CommonResult<Boolean> edit(SysRoleDTO sysRoleDTO);

    /**
     * 编辑角色下的用户
     * @param sysRoleDTO
     * @return
     */
    CommonResult<Boolean> editRoleUserRelation(SysRoleUserDTO sysRoleDTO);

    /**
     * 编辑角色下的菜单
     * @param sysMenuUserDTO
     * @return
     */
    CommonResult<Void> editRoleMenuRelation(SysRoleMenuDTO sysMenuUserDTO);

}
