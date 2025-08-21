package com.example.sea.system.controller;

import com.example.sea.system.service.ISysRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

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


}
