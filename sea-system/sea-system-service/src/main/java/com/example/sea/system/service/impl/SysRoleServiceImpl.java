package com.example.sea.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.PageResult;
import com.example.sea.system.service.ISysRoleService;

import lombok.RequiredArgsConstructor;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.converter.SysRoleConverter;
import com.example.sea.system.dao.SysRoleMapper;
import com.example.sea.system.entity.SysRolePO;
import com.example.sea.system.api.dto.SysRoleDTO;
import com.example.sea.system.api.dto.SysRoleMenuDTO;
import com.example.sea.system.api.dto.SysRoleUserDTO;
import com.example.sea.system.api.param.SysRoleQueryParam;
import com.example.sea.system.api.vo.SysRoleVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 角色表服务实现类
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRolePO> implements ISysRoleService {

    private final SysRoleConverter sysRoleConverter;

    /**
     * 新增角色信息
     */
    @Override
    public CommonResult<Boolean> add(SysRoleDTO sysRoleDTO) {
        SysRolePO sysRole = sysRoleConverter.dtoToEntity(sysRoleDTO);
        boolean result = this.save(sysRole);
        return CommonResult.success(result);
    }

    /**
     * 更新角色信息
     * 更新角色用户关系
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> edit(SysRoleDTO sysRoleDTO) {
        SysRolePO sysRole = sysRoleConverter.dtoToEntity(sysRoleDTO);
        boolean result = this.updateById(sysRole);
        return CommonResult.success(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> editRoleUserRelation(SysRoleUserDTO sysRoleUserDTO) {
        Long roleId = sysRoleUserDTO.getRoleId();
        // 删除该角色下的所有用户
        baseMapper.deleteRoleUsersByRoleId(roleId);
        // 添加新的用户角色关系
        if(!CollectionUtils.isEmpty(sysRoleUserDTO.getUserIdList())){
            baseMapper.insertRoleUsers(roleId, sysRoleUserDTO.getUserIdList());
        }
        return CommonResult.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Void> editRoleMenuRelation(SysRoleMenuDTO sysMenuUserDTO) {
        Long roleId = sysMenuUserDTO.getRoleId();
        // 删除该角色下的所有菜单
        baseMapper.deleteRoleMenusByRoleId(roleId);
        // 添加新的角色菜单关系
        if(!CollectionUtils.isEmpty(sysMenuUserDTO.getMenuIdList())){
            baseMapper.insertRoleMenus(roleId, sysMenuUserDTO.getMenuIdList());
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult<List<SysRoleVO>> list(SysRoleQueryParam sysRoleQueryParam) {
        LambdaQueryWrapper<SysRolePO> wrapper = buildQueryWrapper(sysRoleQueryParam);
        List<SysRolePO> list = this.list(wrapper);
        List<SysRoleVO> voList = list.stream().map(sysRoleConverter::entityToVO).toList();
        return CommonResult.success(voList);
    }

    @Override
    public CommonResult<PageResult<SysRoleVO>> page(SysRoleQueryParam sysRoleQueryParam) {
        LambdaQueryWrapper<SysRolePO> wrapper = buildQueryWrapper(sysRoleQueryParam);
        Page<SysRolePO> page = new Page<>(sysRoleQueryParam.getPageNum(), sysRoleQueryParam.getPageSize());
        Page<SysRolePO> result = this.page(page, wrapper);
        List<SysRoleVO> voList = result.getRecords().stream().map(sysRoleConverter::entityToVO).toList();
        PageResult<SysRoleVO> pageResult = new PageResult<>(voList, result.getTotal(), sysRoleQueryParam.getPageNum(), sysRoleQueryParam.getPageSize());
        return CommonResult.success(pageResult);
    }

    @Override
    public CommonResult<List<String>> getMenuIdsByRoleId(Long roleId) {
        List<String> menuIds = baseMapper.selectMenuIdsByRoleId(roleId);
        return CommonResult.success(menuIds);
    }

    @Override
    public CommonResult<List<String>> getUserIdsByRoleId(Long roleId) {
        List<String> userIds = baseMapper.selectUserIdsByRoleId(roleId);
        return CommonResult.success(userIds);
    }

    private LambdaQueryWrapper<SysRolePO> buildQueryWrapper(SysRoleQueryParam param) {
        LambdaQueryWrapper<SysRolePO> wrapper = Wrappers.lambdaQuery();
        wrapper.like(param.getRoleName() != null, SysRolePO::getRoleName, param.getRoleName())
               .eq(param.getRoleCode() != null, SysRolePO::getRoleCode, param.getRoleCode())
               .eq(param.getStatus() != null, SysRolePO::getStatus, param.getStatus())
               .ge(param.getCreateTimeStart() != null, SysRolePO::getCreateTime, param.getCreateTimeStart())
               .le(param.getCreateTimeEnd() != null, SysRolePO::getCreateTime, param.getCreateTimeEnd())
               .orderByDesc(SysRolePO::getCreateTime);
        return wrapper;
    }

}
