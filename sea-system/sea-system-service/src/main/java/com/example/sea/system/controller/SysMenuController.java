package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.system.interfaces.dto.SysMenuDTO;
import com.example.sea.system.interfaces.vo.SysMenuNodeVO;
import com.example.sea.system.service.ISysMenuService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.validation.annotation.Validated;

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
     * 登录成功后获取菜单树
     * @return
     */
    @GetMapping("/treeMenu")
    public CommonResult<List<SysMenuNodeVO>> treeMenu() {
        return sysMenuService.treeMenu();
    }

    /** 添加菜单 */
    @PostMapping("/add")
    public CommonResult<Boolean> add(@RequestBody @Validated(GroupInsert.class) SysMenuDTO sysMenuDTO) {
        return sysMenuService.add(sysMenuDTO);
    }


}
