package com.example.system.converter;

import org.mapstruct.Mapper;

import com.example.sea.system.interfaces.dto.SysUserDTO;
import com.example.system.entity.SysUser;
/**
 * sys_users 表实体类转换器
 * @author liuhuan
 * @date 2025-05-28
 */
@Mapper(componentModel = "spring")
public interface SysUserConverter {

    /**
     * dto转entity
     * @param dto
     * @return
     */
    public SysUser dtoToEntity(SysUserDTO dto);

}
