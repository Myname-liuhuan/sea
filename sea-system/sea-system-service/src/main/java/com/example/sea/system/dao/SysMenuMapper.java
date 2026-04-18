package com.example.sea.system.dao;

import com.example.sea.system.entity.SysMenuPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单权限表Mapper接口
 * @author admin
 * @date 2025-08-14
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuPO> {

    /**
     * 根据用户ID查询菜单权限
     * @param userId 用户ID
     * @return 菜单权限列表
     */
    List<SysMenuPO> selectMenuListByUserId(Long userId);

}
