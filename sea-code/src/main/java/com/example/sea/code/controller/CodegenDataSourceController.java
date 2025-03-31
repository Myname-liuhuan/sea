package com.example.sea.code.controller;

import com.example.sea.code.service.ICodegenDataSourceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 代码生成 - 数据源信息表控制器
 */
@RestController
@RequestMapping("/codegenDataSource")
public class CodegenDataSourceController {

    @Autowired
    private ICodegenDataSourceService codegenDataSourceService;

}
