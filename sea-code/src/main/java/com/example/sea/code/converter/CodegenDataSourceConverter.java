package com.example.sea.code.converter;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;

/**
 * 数据源信息表bean转换器
 * org.mapstruct工具可以只在接口上加@mapper注解，
 * 不需要关注转化的实现逻辑，
 * 在编译后的class中会自动生成实现类。
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
     * 在字段无法自动映射的情况下，可以使用Mapping注解进行手动映射。
     *  eg：@Mapping(source = "username", target = "name")
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