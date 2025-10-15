package com.example.sea.system.service.impl;

import com.example.sea.system.service.ISysMenuService;

import lombok.RequiredArgsConstructor;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.converter.SysMenuConverter;
import com.example.sea.system.dao.SysMenuMapper;
import com.example.sea.system.entity.SysMenu;
import com.example.sea.system.interfaces.dto.SysMenuDTO;
import com.example.sea.system.interfaces.vo.SysMenuNodeVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 菜单权限表服务实现类
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final SysMenuConverter sysMenuConverter;

    @Override
    public CommonResult<List<SysMenuNodeVO>> treeMenu() {
        //先查出所有菜单
        List<SysMenu> menuList = list();
        List<SysMenuNodeVO> nodeList =
                    menuList.stream()
                    .filter(menu -> Objects.isNull(menu.getParentId()) || menu.getParentId() == 0)
                    .map(menu -> {
                        SysMenuNodeVO rootNode = new SysMenuNodeVO();
                        BeanUtils.copyProperties(menu, rootNode);
                        rootNode.setChildren(buildMenuTree(menuList, rootNode));
                        return rootNode;
                    }).toList();
        return CommonResult.success(nodeList);
    }

    /**
     * 添加菜单
     */
    @Override
    public CommonResult<Boolean> add(SysMenuDTO sysMenuDTO) {
        SysMenu sysMenu = sysMenuConverter.dtoToEntity(sysMenuDTO);
        boolean result = this.save(sysMenu);
        return CommonResult.success(result);
    }

    /**
     * 递归构建菜单树
     * @param menuList 菜单列表
     * @param parentNode 父节点
     * @return  菜单节点列表
     */
    private List<SysMenuNodeVO> buildMenuTree(List<SysMenu> menuList, SysMenuNodeVO parentNode) {
        for (SysMenu menu : menuList) {
            if (menu.getParentId().equals(parentNode.getId())) {
                SysMenuNodeVO childNode = new SysMenuNodeVO();
                BeanUtils.copyProperties(menu, childNode);
                childNode.setChildren(buildMenuTree(menuList, childNode));
                parentNode.getChildren().add(childNode);
            }
        }
        return null;
    }

    
}
