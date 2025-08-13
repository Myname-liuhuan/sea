package com.example.sea.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.system.entity.SysUser;
import com.example.sea.system.interfaces.dto.SysUserQueryDTO;
import com.example.sea.system.interfaces.vo.SysUserVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表Mapper接口
 * @author liuhuan
 * @date 2025-05-28
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户列表
     * @param sysUserQueryDTO
     * @return
     */
    List<SysUserVO> list(SysUserQueryDTO sysUserQueryDTO);

}
