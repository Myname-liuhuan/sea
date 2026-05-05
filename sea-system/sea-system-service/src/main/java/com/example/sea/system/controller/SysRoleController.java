package com.example.sea.system.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;
import com.example.sea.system.api.constants.PermissionConstants;
import com.example.sea.system.api.dto.SysRoleDTO;
import com.example.sea.system.api.dto.SysRoleMenuDTO;
import com.example.sea.system.api.dto.SysRoleUserDTO;
import com.example.sea.system.api.param.SysRoleQueryParam;
import com.example.sea.system.api.vo.SysRoleVO;
import com.example.sea.system.service.ISysRoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

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
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_ADD + "')")
    @Operation(summary = "新增角色", description = "创建新的系统角色，需要传入角色基本信息")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.add(sysRoleDTO);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_EDIT + "')")
    @Operation(summary = "编辑角色", description = "编辑现有角色信息，需要传入完整的角色信息")
    public CommonResult<Boolean> edit(@RequestBody @Validated(GroupUpdate.class) SysRoleDTO sysRoleDTO){
        return sysRoleService.edit(sysRoleDTO);
    }

    /**
     * 编辑角色下的用户
     * @return
     */
    @PostMapping("/editRoleUserRelation")
    @Operation(summary = "编辑角色用户关系", description = "编辑角色下的用户关联关系，可以批量添加或移除角色下的用户")
    public CommonResult<Boolean> editRoleUserRelation(@RequestBody @Validated(GroupUpdate.class) SysRoleUserDTO sysRoleUserDTO){
        return sysRoleService.editRoleUserRelation(sysRoleUserDTO);
    }

    /**
     * 编辑角色下的菜单
     * @param sysMenuUserDTO
     * @return
     */
    @PostMapping("/editRoleMenuRelation")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_EDIT + "')")
    @Operation(summary = "编辑角色菜单关系", description = "编辑角色下的菜单权限关联关系，可以批量添加或移除角色下的菜单权限")
    public CommonResult<Void> editRoleMenuRelation(@RequestBody @Validated(GroupUpdate.class) SysRoleMenuDTO sysMenuUserDTO){
        return sysRoleService.editRoleMenuRelation(sysMenuUserDTO);
    }

    /**
     * 查询角色列表
     * @param sysRoleQueryParam
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_LIST + "')")
    @Operation(summary = "查询角色列表", description = "根据查询条件获取角色列表，支持模糊查询")
    public CommonResult<List<SysRoleVO>> list(SysRoleQueryParam sysRoleQueryParam) {
        return sysRoleService.list(sysRoleQueryParam);
    }

    /**
     * 分页查询角色列表
     * @param sysRoleQueryParam
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_LIST + "')")
    @Operation(summary = "分页查询角色列表", description = "根据查询条件分页获取角色列表，支持模糊查询")
    public CommonResult<PageResult<SysRoleVO>> page(SysRoleQueryParam sysRoleQueryParam) {
        return sysRoleService.page(sysRoleQueryParam);
    }

    @GetMapping("/menuIds/{roleId}")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_LIST + "')")
    @Operation(summary = "获取角色菜单ID列表", description = "根据角色ID查询该角色已分配的菜单ID列表")
    public CommonResult<List<String>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        return sysRoleService.getMenuIdsByRoleId(roleId);
    }

    @GetMapping("/userIds/{roleId}")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_ROLE_LIST + "')")
    @Operation(summary = "获取角色用户ID列表", description = "根据角色ID查询该角色已分配的用户ID列表")
    public CommonResult<List<String>> getUserIdsByRoleId(@PathVariable Long roleId) {
        return sysRoleService.getUserIdsByRoleId(roleId);
    }
}