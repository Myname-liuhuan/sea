package com.example.sea.system.controller;

import com.example.sea.common.result.CommonResult;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.service.ISysUsersService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

/**
 * 用户表控制器
 * @author liuhuan
 * @date 2025-05-28
 */
@RestController
@RequestMapping("/sysUser")
public class SysUsersController {

    private final ISysUsersService sysUsersService;

    @Autowired
    public SysUsersController(ISysUsersService sysUsersService){
        this.sysUsersService = sysUsersService;
    }

    /**
     * 保存用户
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestBody @Validated SysUserDTO sysUserDTO) {
        return sysUsersService.save(sysUserDTO);
    }

    


}
