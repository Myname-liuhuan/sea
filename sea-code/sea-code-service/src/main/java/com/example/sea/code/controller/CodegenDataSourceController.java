package com.example.sea.code.controller;

import com.example.sea.code.api.constants.CodePermissionConstants;
import com.example.sea.code.api.dto.CodeGenDataSourceDTO;
import com.example.sea.code.api.vo.CodegenDataSourceVO;
import com.example.sea.code.api.vo.TableColumnsVO;
import com.example.sea.code.service.ICodegenDataSourceService;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.validation.GroupCheck;
import com.example.sea.common.core.validation.GroupInsert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/codegenDataSource")
@RequiredArgsConstructor
@Tag(name = "数据源管理", description = "代码生成数据源相关操作接口")
public class CodegenDataSourceController {

    private final ICodegenDataSourceService codegenDataSourceService;

    @GetMapping("/checkDataSource")
    @Operation(summary = "测试数据源连接")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.DATASOURCE_CHECK + "')")
    public CommonResult<Boolean> checkDataSource(@Validated(GroupCheck.class) CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.checkDataSource(codeGenDataSourceDTO);
    }

    @PostMapping("/saveDataSource")
    @Operation(summary = "新增数据源")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.DATASOURCE_ADD + "')")
    public CommonResult<Boolean> saveDataSource(@Validated(GroupInsert.class) @RequestBody CodeGenDataSourceDTO codeGenDataSourceDTO) {
        return codegenDataSourceService.saveDataSource(codeGenDataSourceDTO);
    }

    @GetMapping("/listDataSource")
    @Operation(summary = "获取数据源列表")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.DATASOURCE_LIST + "')")
    public CommonResult<List<CodegenDataSourceVO>> listDataSource() {
        return codegenDataSourceService.listDataSource();
    }

    @GetMapping("/listDataBase")
    @Operation(summary = "获取数据库列表")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.CODE_GENERATE + "')")
    public CommonResult<List<String>> listDataBase(Long dataSourceId) {
        return codegenDataSourceService.listDataBase(dataSourceId);
    }

    @GetMapping("/listTable")
    @Operation(summary = "获取表列表")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.CODE_GENERATE + "')")
    public CommonResult<List<String>> listTable(Long dataSourceId, String database) {
        return codegenDataSourceService.listTable(dataSourceId, database);
    }

    @GetMapping("/listColumns")
    @Operation(summary = "获取字段列表")
    @PreAuthorize("hasAuthority('" + CodePermissionConstants.CODE_GENERATE + "')")
    public CommonResult<List<TableColumnsVO>> listColumns(Long dataSourceId, String database, String tableName) {
        return codegenDataSourceService.listColumns(dataSourceId, database, tableName);
    }

}
