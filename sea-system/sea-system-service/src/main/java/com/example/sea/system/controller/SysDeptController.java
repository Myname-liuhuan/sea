package com.example.sea.system.controller;

import com.example.sea.system.service.ISysDeptService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 部门表控制器
 * @author admin
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/sysDept")
public class SysDeptController {

    private final ISysDeptService sysDeptService;

    @Autowired
    public SysDeptController(ISysDeptService sysDeptService){
        this.sysDeptService = sysDeptService;
    }


}
