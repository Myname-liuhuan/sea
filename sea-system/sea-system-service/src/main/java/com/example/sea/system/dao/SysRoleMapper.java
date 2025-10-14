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

    void deleteRoleUsersByRoleId(Long roleId);

    void insertRoleUsers(@Param("roleId") Long roleId, @Param("userIdList") List<Long> userIdList);

}
