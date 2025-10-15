package com.example.sea.system.controller;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.interfaces.dto.SysMenuDTO;
import com.example.sea.system.interfaces.vo.SysMenuNodeVO;
import com.example.sea.system.service.ISysMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 菜单权限表控制器
 * @author admin
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    @Autowired
    public SysMenuController(ISysMenuService sysMenuService){
        this.sysMenuService = sysMenuService;
    }

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
    public CommonResult<Boolean> add(@RequestBody SysMenuDTO sysMenuDTO) {
        return sysMenuService.add(sysMenuDTO);
    }


}
