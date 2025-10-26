package com.example.sea.system.controller;

import com.example.sea.system.service.ISysDeptService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

/**
 * 部门表控制器
 * @author admin
 * @date 2025-08-14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sysDept")
@Tag(name = "部门管理", description = "系统部门相关操作接口")
public class SysDeptController {

    private final ISysDeptService sysDeptService;

}
