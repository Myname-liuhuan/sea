package com.example.sea.system.converter;

import org.mapstruct.Mapper;

import com.example.sea.system.entity.SysMenu;
import com.example.sea.system.interfaces.dto.SysMenuDTO;

/**
 * sys_menu 表实体类转换器
 * @author liuhuan
 * @date 2025-10-15
 */
@Mapper(componentModel = "spring")
public interface SysMenuConverter {
    
    /**
     * dto转entity
     * @param dto
     * @return
     */
    public SysMenu dtoToEntity(SysMenuDTO dto);
}
