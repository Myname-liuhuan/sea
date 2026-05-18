package com.example.sea.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.system.entity.SysLoginLogPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志表Mapper接口
 * @author liuhuan
 * @date 2026-05-18
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLogPO> {

}