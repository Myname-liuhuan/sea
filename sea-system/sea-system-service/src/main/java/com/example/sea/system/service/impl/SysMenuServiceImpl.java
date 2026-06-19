package com.example.sea.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.security.utils.SecurityContextUtil;
import com.example.sea.system.converter.SysMenuConverter;
import com.example.sea.system.dao.SysMenuMapper;
import com.example.sea.system.entity.SysMenuPO;
import com.example.sea.system.api.dto.SysMenuDTO;
import com.example.sea.system.api.vo.SysMenuNodeVO;
import com.example.sea.system.constants.SysMenuTypeEnum;
import com.example.sea.system.api.vo.SysMenuOptionVO;
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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuPO> implements ISysMenuService {

    private final SysMenuConverter sysMenuConverter;

    @Override
    public CommonResult<List<SysMenuNodeVO>> myMenuTree() {
        Long userId = SecurityContextUtil.getUserId();

        //查询当前用户的菜单列表
        List<SysMenuPO> menuList = this.baseMapper.selectMenuListByUserId(userId);

        //再分组
        final Map<Long, List<SysMenuNodeVO>> childrenMap = menuList.stream()
            .collect(Collectors.groupingBy(
                SysMenuPO::getParentId,
                Collectors.mapping(sysMenuConverter::entityToNodeVO, Collectors.toList())
            ));
        return CommonResult.success(buildTreeFromMap(childrenMap, 0L));
    }

    @Override
    public CommonResult<List<SysMenuNodeVO>> allMenuTree() {
        //先查出所有菜单
        List<SysMenuPO> menuList = list();
        //再分组
        final Map<Long, List<SysMenuNodeVO>> childrenMap = menuList.stream()
            .collect(Collectors.groupingBy(
                SysMenuPO::getParentId,
                Collectors.mapping(sysMenuConverter::entityToNodeVO, Collectors.toList())
            ));
        return CommonResult.success(buildTreeFromMap(childrenMap, 0L));
    }

    /**
     * 添加菜单
     */
    @Override
    public CommonResult<Boolean> add(SysMenuDTO sysMenuDTO) {
        SysMenuPO sysMenu = sysMenuConverter.dtoToEntity(sysMenuDTO);
        boolean result = this.save(sysMenu);
        return CommonResult.success(result);
    }

    /**
     * 更新菜单
     */
    @Override
    public CommonResult<Boolean> update(SysMenuDTO sysMenuDTO) {
        if (sysMenuDTO.getId() == null) {
            return CommonResult.failed("菜单ID不能为空");
        }
        SysMenuPO existing = this.getById(sysMenuDTO.getId());
        if (existing == null) {
            return CommonResult.failed("菜单不存在");
        }
        // 不能将自己设为自己的父菜单
        if (sysMenuDTO.getId().equals(sysMenuDTO.getParentId())) {
            return CommonResult.failed("不能将自己设为自己的父菜单");
        }
        SysMenuPO sysMenu = sysMenuConverter.dtoToEntity(sysMenuDTO);
        boolean result = this.updateById(sysMenu);
        return CommonResult.success(result);
    }

    /**
     * 删除菜单（软删除）：存在子菜单则拒绝
     */
    @Override
    public CommonResult<Boolean> delete(Long id) {
        if (id == null) {
            return CommonResult.failed("菜单ID不能为空");
        }
        LambdaQueryWrapper<SysMenuPO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysMenuPO::getParentId, id);
        if (this.baseMapper.selectCount(childWrapper) > 0) {
            return CommonResult.failed("该菜单下存在子菜单，请先删除子菜单");
        }
        boolean result = this.removeById(id);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<List<SysMenuOptionVO>> options() {
        LambdaQueryWrapper<SysMenuPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenuPO::getMenuType, SysMenuTypeEnum.DIRECTORY.getCode(), SysMenuTypeEnum.MENU.getCode())
            .orderByAsc(SysMenuPO::getOrderNum);
        List<SysMenuOptionVO> list = list(wrapper).stream()
            .map(sysMenuConverter::entityToOptionVO)
            .collect(Collectors.toList());
        return CommonResult.success(list);
    }

    @Override
    public List<Long> listAllParentIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return List.of();
        }
        List<Long> result = new ArrayList<>(menuIds);
        List<Long> currentLevel = new ArrayList<>(menuIds);

        while (!currentLevel.isEmpty()) {
            LambdaQueryWrapper<SysMenuPO> wrapper = Wrappers.lambdaQuery();
            wrapper.in(SysMenuPO::getId, currentLevel)
                .isNotNull(SysMenuPO::getParentId)
                .ne(SysMenuPO::getParentId, 0L)
                .select(SysMenuPO::getParentId);

            List<Long> parentIds = this.baseMapper.selectObjs(wrapper).stream()
                .map(obj -> ((Number) obj).longValue())
                .distinct()
                .filter(id -> !result.contains(id))
                .collect(Collectors.toList());

            if (parentIds.isEmpty()) {
                break;
            }
            result.addAll(parentIds);
            currentLevel = parentIds;
        }
        return result;
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
