package com.example.sea.system.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import com.example.sea.common.security.entity.LoginUser;
import com.example.sea.system.api.constants.PermissionConstants;
import com.example.sea.system.api.dto.SysUserDTO;
import com.example.sea.system.api.param.SysUserQueryParam;
import com.example.sea.system.api.vo.SysUserVO;
import com.example.sea.system.service.ISysUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 用户表控制器
 * @author liuhuan
 * @date 2025-05-28
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysUser")
@Tag(name = "用户管理", description = "系统用户相关操作接口")
public class SysUserController {

    private final ISysUserService sysUsersService;

    /**
     * 新增用户
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_USER_ADD + "')")
    @Operation(summary = "新增用户", description = "创建新的系统用户，需要传入用户基本信息")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysUserDTO sysUserDTO) {
        return sysUsersService.add(sysUserDTO);
    }

    /**
     * 更新用户信息
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_USER_EDIT + "')")
    @Operation(summary = "更新用户信息",description = "更新现有用户的基本信息，需要传入完整的用户信息")
    public CommonResult<Boolean> update(@RequestBody @Validated(GroupUpdate.class) SysUserDTO sysUserDTO) {
        return sysUsersService.update(sysUserDTO);
    }

    /**
     * 分页查询用户列表
     * @param sysUserQueryParam
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_USER_LIST + "')")
    @Operation(summary = "分页查询用户列表", description = "根据查询条件分页获取用户列表，支持模糊查询")
    public CommonResult<PageResult<SysUserVO>> page(SysUserQueryParam sysUserQueryParam) {
        return sysUsersService.page(sysUserQueryParam);
    }

    /**
     * 查询用户列表
     * @param sysUserQueryParam
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_USER_LIST + "')")
    @Operation(summary = "查询用户列表", description = "根据查询条件获取用户列表，支持模糊查询")
    public CommonResult<List<SysUserVO>> list(SysUserQueryParam sysUserQueryParam) {
        return sysUsersService.list(sysUserQueryParam);
    }

    /**
     * 根据用户名获取登录用户信息
     *
     * ::@RequestParam String username 要求请求参数里必须有 username 这个名字的参数。
            当传的是 usname=888,没有匹配到 username。
            SpringMVC 在绑定阶段就发现 缺少必须参数，于是直接抛出 MissingServletRequestParameterException → 400 Bad Request。
            此时，参数压根没绑定到方法，也就不会走 Hibernate Validator 的 @NotBlank
        解决:1,使用实体类接收参数,在实体类中加注解; 2,手动去判断参数是否为空
     * @param username
     * @return
     */
    @GetMapping("/getLoginUser")
    @Operation(summary = "获取登录用户信息", description = "根据用户名获取用户的登录信息，包括权限和角色信息")
    public CommonResult<LoginUser> getLoginUser(String username) {
        return sysUsersService.getLoginUser(username);
    }


}
