package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import com.example.sea.system.interfaces.dto.SysRoleDTO;
import com.example.sea.system.service.ISysRoleService;

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
    @PostMapping("/editRoleUsers")
    public CommonResult<Boolean> editRoleUsers(@RequestBody @Validated(GroupUpdate.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.editRoleUsers(sysRoleDTO);
    }
}
