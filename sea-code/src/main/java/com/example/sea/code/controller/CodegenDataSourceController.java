package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.result.CommonResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.example.sea.code.entity.dto.GroupCheck;
import com.example.sea.code.entity.dto.GroupSave;
import com.example.sea.code.entity.CodegenDataSource;

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
     * @return Mono<Boolean>
     */
    @GetMapping("/checkDataSource")
    public Mono<Boolean> checkDataSource(@Validated(GroupCheck.class) CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.checkDataSource(codeGenDataSourceDTO);
    }

    /**
     * 新增数据源
     * @return Mono<Boolean>
     */
    @PostMapping("/saveDataSource")
    public Mono<Boolean> saveDataSource(@Validated(GroupSave.class) @RequestBody CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.saveDataSource(codeGenDataSourceDTO);
    }

    /**
     * 获取数据源列表
     */
    @GetMapping("/listDataSource")
    public Flux<CodegenDataSource> listDataSource() {
        return codegenDataSourceService.listDataSource();
    }
}
