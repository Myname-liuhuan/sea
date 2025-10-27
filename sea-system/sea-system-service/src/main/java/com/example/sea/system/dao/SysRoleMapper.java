package com.example.sea.system.dao;

import com.example.sea.system.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表Mapper接口
 * @author admin
 * @date 2025-08-14
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 通过角色ID删除角色和用户关联
     * @param roleId
     */
    void deleteRoleUsersByRoleId(Long roleId);

    /**
     * 批量新增角色用户关联
     * @param roleId
     * @param userIdList
     */
    void insertRoleUsers(@Param("roleId") Long roleId, @Param("userIdList") List<Long> userIdList);

    /**
     * 通过角色ID删除角色和菜单关联
     * @param roleId
     */
    void deleteRoleMenusByRoleId(Long roleId);

    /**
     * 批量新增角色菜单关联
     * @param roleId
     * @param menuIdList
     */
    void insertRoleMenus(@Param("roleId") Long roleId,@Param("menuIdList") List<Long> menuIdList);

}
