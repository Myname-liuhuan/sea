package com.example.sea.system.converter;

import com.example.sea.log.api.dto.OperationLogDTO;
import com.example.sea.system.entity.SysOperationLogPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 使用mapstruct 因为其效率远大于spring的beanutils.copyProperties
 * 因为它是编译期生成纯 Java 代码，而 BeanUtils 是运行时反射复制。
 * MapStruct 几乎没有运行时开销，性能可提升 几十倍到上百倍。
 * 操作日志实体类转换器
 * @author liuhuan
 * @date 2025-05-18
 */
@Mapper(componentModel = "spring")
public interface SysOperationLogConverter {

    /**
     * DTO转PO
     * @param dto 操作日志DTO
     * @return 操作日志PO
     */
    @Mapping(target = "id", ignore = true)
    SysOperationLogPO dtoToPo(OperationLogDTO dto);
}