package com.example.sea.system.converter;

import org.mapstruct.Mapper;

import com.example.sea.system.entity.SysMenuPO;
import com.example.sea.system.api.dto.SysMenuDTO;
import com.example.sea.system.api.vo.SysMenuNodeVO;
import com.example.sea.system.api.vo.SysMenuOptionVO;

import org.mapstruct.Mapping;

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
    SysMenuPO dtoToEntity(SysMenuDTO dto);

    /**
     * entity转nodeVO
     * @param entity
     * @return
     */
    SysMenuNodeVO entityToNodeVO(SysMenuPO entity);

    @Mapping(source = "id", target = "menuId")
    SysMenuOptionVO entityToOptionVO(SysMenuPO entity);
}
