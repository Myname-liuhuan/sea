package com.example.sea.code.converter;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.sea.code.entity.CodegenDataSourcePO;
import com.example.sea.code.api.dto.CodeGenDataSourceDTO;
import com.example.sea.code.api.vo.CodegenDataSourceVO;

@Mapper(componentModel = "spring")
public interface CodegenDataSourceConverter {

    CodegenDataSourceVO entityToVo(CodegenDataSourcePO codegenDataSource);

    List<CodegenDataSourceVO> entityToVo(List<CodegenDataSourcePO> list);

    CodegenDataSourcePO voToEntity(CodegenDataSourceVO codegenDataSourceVO);

    CodegenDataSourcePO dtoToEntity(CodeGenDataSourceDTO dto);
}
