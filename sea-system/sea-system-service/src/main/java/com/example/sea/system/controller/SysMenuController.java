package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.system.api.constants.PermissionConstants;
import com.example.sea.system.api.dto.SysMenuDTO;
import com.example.sea.system.api.vo.SysMenuNodeVO;
import com.example.sea.system.service.ISysMenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单权限表控制器
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysMenu")
@Tag(name = "菜单管理", description = "系统菜单相关操作接口")
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    /**
     * 当前登录用户的菜单树
     * @return
     */
    @GetMapping("/myMenuTree")
    @Operation(summary = "获取当前用户菜单树", description = "获取当前登录用户的菜单权限树")
    public CommonResult<List<SysMenuNodeVO>> myMenuTree() {
        return sysMenuService.myMenuTree();
    }

    /**
     * 获取所有菜单树
     * @return
     */
    @GetMapping("/allMenuTree")
    @Operation(summary = "获取所有菜单树", description = "获取系统中所有的菜单权限树")
    public CommonResult<List<SysMenuNodeVO>> allMenuTree() {
        return sysMenuService.allMenuTree();
    }

    /** 添加菜单 */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYS_MENU_ADD + "')")
    @Operation(summary = "新增菜单", description = "创建新的菜单权限，需要传入菜单基本信息")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysMenuDTO sysMenuDTO) {
        return sysMenuService.add(sysMenuDTO);
    }


}
