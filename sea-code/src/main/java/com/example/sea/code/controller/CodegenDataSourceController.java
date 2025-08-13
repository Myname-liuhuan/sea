package com.example.sea.code.controller;

import com.example.sea.code.entity.dto.CodeGenDataSourceDTO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.core.result.CommonResult;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import com.example.sea.code.entity.dto.GroupCheck;
import com.example.sea.code.entity.dto.GroupSave;
import com.example.sea.code.entity.vo.CodegenDataSourceVO;
import com.example.sea.code.entity.vo.TableColumnsVO;

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

    /**
     * 获取数据源列表
     */
    @GetMapping("/listDataSource")
    public Mono<CommonResult<List<CodegenDataSourceVO>>> listDataSource() {
        return Mono.just(codegenDataSourceService.listDataSource());
    }

    /**
     * 根据数据源获取数据库列表
     * @param dataSourceId 数据源ID
     * @return Mono<CommonResult<List<String>>>
     */
    @GetMapping("/listDataBase")
    public Mono<CommonResult<List<String>>> listDataBase(Long dataSourceId) {
        return Mono.just(codegenDataSourceService.listDataBase(dataSourceId));
    }

    /**
     * 根据数据源和数据库获取表列表
     * @param dataSourceId
     * @param database
     * @return
     */
    @GetMapping("/listTable")
    public Mono<CommonResult<List<String>>> listTable(Long dataSourceId, String database) {
        return Mono.just(codegenDataSourceService.listTable(dataSourceId, database));
    }

    /**
     * 根据数据源、数据库和表名获取字段列表
     * @param dataSourceId 数据源ID
     * @param database 数据库名
     * @param tableName 表名
     * @return Mono<CommonResult<List<String>>>
     */
    @GetMapping("/listColumns")     
    public Mono<CommonResult<List<TableColumnsVO>>> listColumns(Long dataSourceId, String database, String tableName) {
        return Mono.just(codegenDataSourceService.listColumns(dataSourceId, database, tableName));
    }


     
}
