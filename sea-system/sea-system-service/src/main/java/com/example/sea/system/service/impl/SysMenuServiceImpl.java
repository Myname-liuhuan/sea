package com.example.sea.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.system.converter.SysMenuConverter;
import com.example.sea.system.dao.SysMenuMapper;
import com.example.sea.system.entity.SysMenu;
import com.example.sea.system.api.dto.SysMenuDTO;
import com.example.sea.system.api.vo.SysMenuNodeVO;
import com.example.sea.system.service.ISysMenuService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 菜单权限表服务实现类
 * @author admin
 * @date 2025-08-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final SysMenuConverter sysMenuConverter;

    @Override
    public CommonResult<List<SysMenuNodeVO>> myMenuTree() {
        Long userId = SecurityContextUtil.getUserId();
        
        //查询当前用户的菜单列表
        List<SysMenu> menuList = this.baseMapper.selectMenuListByUserId(userId);
        
        //再分组
        final Map<Long, List<SysMenuNodeVO>> childrenMap = menuList.stream()
            .collect(Collectors.groupingBy(
                SysMenu::getParentId,
                Collectors.mapping(sysMenuConverter::entityToNodeVO, Collectors.toList())
            ));
        return CommonResult.success(buildTreeFromMap(childrenMap, 0L));
    }

    @Override
    public CommonResult<List<SysMenuNodeVO>> allMenuTree() {
        //先查出所有菜单
        List<SysMenu> menuList = list();
        //再分组
        final Map<Long, List<SysMenuNodeVO>> childrenMap = menuList.stream()
            .collect(Collectors.groupingBy(
                SysMenu::getParentId,
                Collectors.mapping(sysMenuConverter::entityToNodeVO, Collectors.toList())
            ));
        return CommonResult.success(buildTreeFromMap(childrenMap, 0L));
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
     * 从Map中递归构建树结构
     * @param childrenMap parentId到子节点的映射
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    private List<SysMenuNodeVO> buildTreeFromMap(Map<Long, List<SysMenuNodeVO>> childrenMap, Long parentId) {
        List<SysMenuNodeVO> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        children.forEach(child -> 
            child.setChildren(buildTreeFromMap(childrenMap, child.getId()))
        );
        return children;
    }

    
}
