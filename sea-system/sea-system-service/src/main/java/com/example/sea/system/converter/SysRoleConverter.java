package com.example.sea.system.converter;

import org.mapstruct.Mapper;

import com.example.sea.system.entity.SysRolePO;
import com.example.sea.system.api.dto.SysRoleDTO;

/**
 * sys_role 表实体类转换器
 * @author liuhuan
 * @date 2025-10-14
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter {

    /**
     * dto转entity
     * @param dto
     * @return
     */
    public SysRolePO dtoToEntity(SysRoleDTO dto);

}
