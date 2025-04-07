package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.result.CommonResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import com.example.sea.code.entity.dto.GroupCheck;
import com.example.sea.code.entity.dto.GroupSave;

/**
 * 代码生成 - 数据源信息表控制器
 * @author liuhuan
 * @date 2025-03-31
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
     * @return Mono<CommonResult<Boolean>>
     */
    @GetMapping("/checkDataSource")
    public Mono<CommonResult<Boolean>> checkDataSource(@Validated(GroupCheck.class) CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return Mono.just(codegenDataSourceService.checkDataSource(codeGenDataSourceDTO));
    }

    /**
     * 新增数据源
     * @return Mono<CommonResult<Boolean>>
     */
    @PostMapping("/saveDataSource")
    public Mono<CommonResult<Boolean>> saveDataSource(@Validated(GroupSave.class) @RequestBody CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return Mono.just(codegenDataSourceService.saveDataSource(codeGenDataSourceDTO));
    }
}
