package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.system.entity.SysRolePO;
import com.example.sea.system.api.dto.SysRoleDTO;
import com.example.sea.system.api.dto.SysRoleMenuDTO;
import com.example.sea.system.api.dto.SysRoleUserDTO;
import com.example.sea.system.api.param.SysRoleQueryParam;
import com.example.sea.system.api.vo.SysRoleVO;

/**
 * 角色表服务接口
 * @author admin
 * @date 2025-08-14
 */
public interface ISysRoleService extends IService<SysRolePO> {

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

    /**
     * 查询角色列表
     * @param sysRoleQueryParam 查询参数
     * @return 角色列表
     */
    CommonResult<List<SysRoleVO>> list(SysRoleQueryParam sysRoleQueryParam);

    /**
     * 分页查询角色列表
     * @param sysRoleQueryParam 查询参数
     * @return 分页结果
     */
    CommonResult<PageResult<SysRoleVO>> page(SysRoleQueryParam sysRoleQueryParam);

}
