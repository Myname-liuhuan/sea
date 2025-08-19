package com.example.sea.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupSave;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.sea.system.interfaces.dto.SysUserQueryDTO;
import com.example.sea.system.interfaces.vo.SysUserVO;
import com.example.sea.system.service.ISysUserService;

/**
 * 用户表控制器
 * @author liuhuan
 * @date 2025-05-28
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    private final ISysUserService sysUsersService;

    @Autowired
    public SysUserController(ISysUserService sysUsersService){
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

    /**
     * 根据用户名获取登录用户信息
     * 
     * @RequestParam String username 要求请求参数里必须有 username 这个名字的参数。
            你传的是 usname=888，没有匹配到 username。
            SpringMVC 在绑定阶段就发现 缺少必须参数，于是直接抛出 MissingServletRequestParameterException → 400 Bad Request。
            此时，参数压根没绑定到方法，也就不会走 Hibernate Validator 的 @NotBlank
     * @param username
     * @return
     */
    @GetMapping("/getLoginUser")
    public CommonResult<LoginUser> getLoginUser(String username) {
        return sysUsersService.getLoginUser(username);
    }


}
