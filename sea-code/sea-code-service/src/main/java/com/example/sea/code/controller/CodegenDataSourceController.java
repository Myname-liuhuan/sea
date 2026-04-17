package com.example.sea.code.controller;

import com.example.sea.code.api.dto.CodeGenDataSourceDTO;
import com.example.sea.code.api.dto.CodeGenerateDTO;
import com.example.sea.code.api.dto.GroupCheck;
import com.example.sea.code.api.dto.GroupSave;
import com.example.sea.code.api.vo.CodegenDataSourceVO;
import com.example.sea.code.api.vo.TableColumnsVO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.code.service.ICodeGenerationService;
import com.example.sea.common.core.result.CommonResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ContentDisposition;

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
     * @return CommonResult<Boolean>
     */
    @GetMapping("/checkDataSource")
    public CommonResult<Boolean> checkDataSource(@Validated(GroupCheck.class) CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.checkDataSource(codeGenDataSourceDTO);
    }

    /**
     * 新增数据源
     * @return CommonResult<Boolean>
     */
    @PostMapping("/saveDataSource")
    public CommonResult<Boolean> saveDataSource(@Validated(GroupSave.class) @RequestBody CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.saveDataSource(codeGenDataSourceDTO);
    }

    /**
     * 获取数据源列表
     */
    @GetMapping("/listDataSource")
    public CommonResult<List<CodegenDataSourceVO>> listDataSource() {
        return codegenDataSourceService.listDataSource();
    }

    /**
     * 根据数据源获取数据库列表
     * @param dataSourceId 数据源ID
     * @return CommonResult<List<String>>
     */
    @GetMapping("/listDataBase")
    public CommonResult<List<String>> listDataBase(Long dataSourceId) {
        return codegenDataSourceService.listDataBase(dataSourceId);
    }

    /**
     * 根据数据源和数据库获取表列表
     * @param dataSourceId
     * @param database
     * @return
     */
    @GetMapping("/listTable")
    public CommonResult<List<String>> listTable(Long dataSourceId, String database) {
        return codegenDataSourceService.listTable(dataSourceId, database);
    }

    /**
     * 根据数据源、数据库和表名获取字段列表
     * @param dataSourceId 数据源ID
     * @param database 数据库名
     * @param tableName 表名
     * @return CommonResult<List<TableColumnsVO>>
     */
    @GetMapping("/listColumns")
    public CommonResult<List<TableColumnsVO>> listColumns(Long dataSourceId, String database, String tableName) {
        return codegenDataSourceService.listColumns(dataSourceId, database, tableName);
    }


}
