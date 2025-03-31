package com.example.sea.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.common.result.CommonResult;

/**
 * 代码生成 - 数据源信息表服务接口
 */
public interface ICodegenDataSourceService extends IService<CodegenDataSource> {

    CommonResult<Boolean> checkDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO);

}
