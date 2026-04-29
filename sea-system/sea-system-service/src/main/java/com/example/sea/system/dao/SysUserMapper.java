package com.example.sea.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.system.entity.SysUserPO;
import com.example.sea.system.api.param.SysUserQueryParam;
import com.example.sea.system.api.vo.SysUserVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表Mapper接口
 * @author liuhuan
 * @date 2025-05-28
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserPO> {

    /**
     * 查询用户总数（用于分页）
     * @param sysUserQueryParam 查询参数
     * @return 用户总数
     */
    Long count(@Param("dto") SysUserQueryParam sysUserQueryParam);

     /**
     * 根据用户ID获取角色编码列表
     * @param userId
     * @return
     */
    List<String> getRoleCodeByUserId(Long userId);

    /**
     * 根据用户ID获取权限列表
     * @param userId
     * @return
     */
    List<String> getPermsByUserId(Long userId);

}
