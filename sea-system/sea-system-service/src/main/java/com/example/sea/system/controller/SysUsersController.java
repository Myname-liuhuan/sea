package com.example.sea.system.controller;

import com.example.sea.common.result.CommonResult;
import com.example.sea.common.validation.GroupInsert;
import com.example.sea.common.validation.GroupSave;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.interfaces.dto.SysUserQueryDTO;
import com.example.sea.system.interfaces.vo.SysUserVO;
import com.example.sea.system.service.ISysUsersService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 新增用户
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/save")
    public CommonResult<Boolean> save(@RequestBody @Validated(GroupSave.class) SysUserDTO sysUserDTO) {
        return sysUsersService.save(sysUserDTO);
    }

    /**
     * 更新用户信息
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody @Validated(GroupInsert.class) SysUserDTO sysUserDTO) {
        return sysUsersService.update(sysUserDTO);
    }

    /**
     * 查询用户列表
     * @param sysUserQueryDTO
     * @return
     */         
    @GetMapping("/list")
    public CommonResult<List<SysUserVO>> list(SysUserQueryDTO sysUserQueryDTO) {
        return sysUsersService.list(sysUserQueryDTO);
    }


}
