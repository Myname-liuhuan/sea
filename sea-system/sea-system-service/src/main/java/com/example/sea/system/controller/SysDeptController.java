package com.example.sea.system.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.dto.SysDeptDTO;
import com.example.sea.system.api.vo.SysDeptVO;
import com.example.sea.system.service.ISysDeptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * 部门表控制器
 * @author admin
 * @date 2025-08-14
 */
@Validated
@RestController
@RequestMapping("/sysDept")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "系统部门相关操作接口")
public class SysDeptController {

    private final ISysDeptService sysDeptService;

    @GetMapping("/tree")
    @Operation(summary = "获取部门树", description = "获取所有部门的树形结构")
    public CommonResult<List<SysDeptVO>> tree() {
        return sysDeptService.tree();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取部门详情", description = "根据ID获取部门详情")
    public CommonResult<SysDeptVO> getById(@PathVariable @NotNull Long id) {
        return sysDeptService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增部门", description = "新增一个部门")
    public CommonResult<Void> add(@RequestBody @Validated SysDeptDTO dto) {
        return sysDeptService.add(dto);
    }

    @PutMapping
    @Operation(summary = "更新部门", description = "更新部门信息")
    public CommonResult<Void> update(@RequestBody @Validated SysDeptDTO dto) {
        return sysDeptService.update(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门", description = "删除指定部门")
    public CommonResult<Void> delete(@PathVariable @NotNull Long id) {
        return sysDeptService.delete(id);
    }

    @GetMapping("/treeSelect")
    @Operation(summary = "获取部门下拉树", description = "获取部门下拉树形结构，用于选择")
    public CommonResult<List<SysDeptVO>> treeSelect() {
        return sysDeptService.treeSelect();
    }
}
