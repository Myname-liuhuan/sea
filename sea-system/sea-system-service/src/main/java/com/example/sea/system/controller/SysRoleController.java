package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import com.example.sea.system.interfaces.dto.SysRoleDTO;
import com.example.sea.system.interfaces.dto.SysRoleMenuDTO;
import com.example.sea.system.interfaces.dto.SysRoleUserDTO;
import com.example.sea.system.service.ISysRoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

/**
 * 角色表控制器
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysRole")
@Tag(name = "角色管理", description = "系统角色相关操作接口")
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    @PostMapping("/add")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.add(sysRoleDTO);
    }

    @PostMapping("/edit")
    public CommonResult<Boolean> edit(@RequestBody @Validated(GroupUpdate.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.edit(sysRoleDTO);
    }

    /**
     * 编辑角色下的用户
     * @param sysRoleDTO
     * @return
     */
    @PostMapping("/editRoleUserRelation")
    public CommonResult<Boolean> editRoleUserRelation(@RequestBody @Validated(GroupUpdate.class) SysRoleUserDTO sysRoleUserDTO){
        return sysRoleService.editRoleUserRelation(sysRoleUserDTO);
    }

    /**
     * 编辑角色下的菜单
     * @param sysMenuUserDTO
     * @return
     */
    @PostMapping("/editRoleMenuRelation")
    public CommonResult<Void> editRoleMenuRelation(@RequestBody @Validated(GroupUpdate.class) SysRoleMenuDTO sysMenuUserDTO){
        return sysRoleService.editRoleMenuRelation(sysMenuUserDTO);
    }
}
