package com.example.sea.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.system.entity.SysUser;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表Mapper接口
 */
@Mapper
public interface SysUsersMapper extends BaseMapper<SysUser> {

}
