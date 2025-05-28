package com.example.system.controller;

import com.example.sea.common.result.CommonResult;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.system.service.ISysUsersService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户表控制器
 */
@RestController
@RequestMapping("/sysUsers")
public class SysUsersController {

    private final ISysUsersService sysUsersService;

    @Autowired
    public SysUsersController(ISysUsersService sysUsersService){
        this.sysUsersService = sysUsersService;
    }

    @PostMapping("/save")
    public CommonResult<Boolean> save(SysUserDTO sysUserDTO) {
        return CommonResult.success(sysUsersService.save(sysUserDTO));
    }

    


}
