package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.entity.SysMenu;
import com.example.sea.system.interfaces.dto.SysMenuDTO;
import com.example.sea.system.interfaces.vo.SysMenuNodeVO;

/**
 * 菜单权限表服务接口
 * @author admin
 * @date 2025-08-14
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取当前用户的菜单树
     * @return
     */
    CommonResult<List<SysMenuNodeVO>> myMenuTree();

    /**
     * 获取菜单树
     * @return
     */
    CommonResult<List<SysMenuNodeVO>> allMenuTree();

    /**
     * 添加菜单
     * @param sysMenuDTO
     * @return
     */
    CommonResult<Boolean> add(SysMenuDTO sysMenuDTO);

}
