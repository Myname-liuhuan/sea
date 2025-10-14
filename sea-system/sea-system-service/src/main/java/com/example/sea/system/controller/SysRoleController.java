package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import com.example.sea.system.interfaces.dto.SysRoleDTO;
import com.example.sea.system.service.ISysRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

/**
 * 角色表控制器
 * @author admin
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    @Autowired
    public SysRoleController(ISysRoleService sysRoleService){
        this.sysRoleService = sysRoleService;
    }

    @PostMapping("/add")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.add(sysRoleDTO);
    }

    @PostMapping("/edit")
    public CommonResult<Boolean> edit(@RequestBody @Validated(GroupUpdate.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.edit(sysRoleDTO);
    }
}
