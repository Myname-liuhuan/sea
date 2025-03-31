package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.result.CommonResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

/**
 * 代码生成 - 数据源信息表控制器
 */
@RestController
@RequestMapping("/codegenDataSource")
public class CodegenDataSourceController {

    private final ICodegenDataSourceService codegenDataSourceService;

    @Autowired
    public CodegenDataSourceController(ICodegenDataSourceService codegenDataSourceService) {
        this.codegenDataSourceService = codegenDataSourceService;
    }

    /**
     * 测试数据源连接
     * @return CommonResult<Boolean>
     */
    @GetMapping("/checkDataSource")
    public CommonResult<Boolean> checkDataSource(@Validated CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.checkDataSource(codeGenDataSourceDTO);
    }



}
