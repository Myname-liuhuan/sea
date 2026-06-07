package com.example.sea.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.entity.SysMenuPO;
import com.example.sea.system.api.dto.SysMenuDTO;
import com.example.sea.system.api.vo.SysMenuNodeVO;
import com.example.sea.system.api.vo.SysMenuOptionVO;

/**
 * 菜单权限表服务接口
 * @author admin
 * @date 2025-08-14
 */
public interface ISysMenuService extends IService<SysMenuPO> {

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

    /**
     * 获取菜单选项列表（平铺）
     * @return
     */
    CommonResult<List<SysMenuOptionVO>> options();

    /**
     * 根据菜单ID列表获取所有父节点ID（包括自己）
     * 用于角色菜单编辑时，叶子节点需要补全父节点链条
     * @param menuIds 菜单ID列表
     * @return 包含自己及所有父节点ID的列表
     */
    List<Long> listAllParentIds(List<Long> menuIds);

}
