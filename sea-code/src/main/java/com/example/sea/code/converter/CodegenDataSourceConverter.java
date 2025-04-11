package com.example.sea.code.converter;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;

/**
 * 数据源信息表bean转换器
 * @author liuhuan
 * @date 2025-04-11
 */
@Mapper(componentModel = "spring")
public interface CodegenDataSourceConverter {

    /**
     * 数据源信息表实体类转换为数据源信息表视图对象
     * @param codegenDataSource 数据源信息表实体类
     * @return 数据源信息表视图对象
     */
    CodegenDataSourceVO entityToVo(CodegenDataSource codegenDataSource);

    /**
     * 实体类列表转换为视图对象列表
     * @param list
     * @return
     */
    List<CodegenDataSourceVO> entityToVo(List<CodegenDataSource> list);


    /**
     * 数据源信息表视图对象转换为数据源信息表实体类
     * @param codegenDataSourceVO 数据源信息表视图对象
     * @return 数据源信息表实体类
     */
    CodegenDataSource voToEntity(CodegenDataSourceVO codegenDataSourceVO);
} 