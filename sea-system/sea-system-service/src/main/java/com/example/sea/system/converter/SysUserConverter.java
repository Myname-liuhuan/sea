package com.example.sea.system.converter;

import com.example.sea.system.api.vo.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sea.system.entity.SysUserPO;
import com.example.sea.system.api.dto.SysUserDTO;

import java.util.List;

/**
 * 使用mapstruct 因为其效率远大于spring的beanutils.copyProperties
 * 因为它是编译期生成纯 Java 代码，而 BeanUtils 是运行时反射复制。
MapStruct 几乎没有运行时开销，性能可提升 几十倍到上百倍。
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
    @Mapping(target = "passwordHash", source = "password")
    SysUserPO dtoToEntity(SysUserDTO dto);

    List<SysUserVO> convertPoListToVoList(List<SysUserPO> userList);
}
