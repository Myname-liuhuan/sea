package com.example.sea.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sea.code.entity.CodegenDataSource;
import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;
import com.example.sea.common.result.CommonResult;
import java.util.List;
import reactor.core.publisher.Flux;

/**
 * 代码生成 - 数据源信息表服务接口
 * @author liuhuan
 * @date 2025-03-31
 * @description 代码生成 - 数据源信息表服务接口
 */
public interface ICodegenDataSourceService extends IService<CodegenDataSource> {

    /**
     * 校验数据源是否有效
     * @param codeGenDataSourceDTO
     * @return
     */
    CommonResult<Boolean> checkDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO);

    /**
     * 保存数据源信息
     * @param codeGenDataSourceDTO
     * @return
     */
    CommonResult<Boolean> saveDataSource(CodeGenDataSourceDTO codeGenDataSourceDTO);

    /**
     * 获取数据源列表
     * @return 数据源列表
     */
    CommonResult<List<CodegenDataSourceVO>> listDataSource();
}
