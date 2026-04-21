package com.example.sea.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.common.core.result.ResultCode;
import com.example.sea.system.api.dto.SysDeptDTO;
import com.example.sea.system.api.vo.SysDeptVO;
import com.example.sea.system.dao.SysDeptMapper;
import com.example.sea.system.entity.SysDeptPO;
import com.example.sea.system.service.ISysDeptService;

import lombok.RequiredArgsConstructor;

/**
 * 部门表服务实现类
 * @author admin
 * @date 2025-08-14
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDeptPO> implements ISysDeptService {

    @Override
    public CommonResult<List<SysDeptVO>> tree() {
        List<SysDeptPO> allDepts = list();
        List<SysDeptVO> tree = buildTree(allDepts, 0L);
        return CommonResult.success(tree);
    }

    @Override
    public CommonResult<SysDeptVO> getById(Long id) {
        SysDeptPO dept = baseMapper.selectById(id);
        if (dept == null) {
            return CommonResult.failed("部门不存在");
        }
        return CommonResult.success(convertToVO(dept));
    }

    @Override
    public CommonResult<Void> add(SysDeptDTO dto) {
        // 检查部门名称是否重复
        LambdaQueryWrapper<SysDeptPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDeptPO::getName, dto.getName())
               .eq(SysDeptPO::getParentId, dto.getParentId());
        if (baseMapper.selectCount(wrapper) > 0) {
            return CommonResult.failed("同级部门下已存在同名部门");
        }

        SysDeptPO dept = new SysDeptPO();
        dept.setParentId(dto.getParentId());
        dept.setName(dto.getName());
        dept.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        dept.setLeader(dto.getLeader());
        dept.setMobile(dto.getMobile());
        dept.setEmail(dto.getEmail());
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : "1");
        baseMapper.insert(dept);
        return CommonResult.success();
    }

    @Override
    public CommonResult<Void> update(SysDeptDTO dto) {
        if (dto.getId() == null) {
            return CommonResult.failed("部门ID不能为空");
        }

        SysDeptPO dept = baseMapper.selectById(dto.getId());
        if (dept == null) {
            return CommonResult.failed("部门不存在");
        }

        // 检查部门名称是否重复（排除自己）
        LambdaQueryWrapper<SysDeptPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDeptPO::getName, dto.getName())
               .eq(SysDeptPO::getParentId, dto.getParentId())
               .ne(SysDeptPO::getId, dto.getId());
        if (baseMapper.selectCount(wrapper) > 0) {
            return CommonResult.failed("同级部门下已存在同名部门");
        }

        // 不能将自己设为自己的子部门
        if (dto.getId().equals(dto.getParentId())) {
            return CommonResult.failed("不能将自己设为自己的父部门");
        }

        dept.setParentId(dto.getParentId());
        dept.setName(dto.getName());
        dept.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        dept.setLeader(dto.getLeader());
        dept.setMobile(dto.getMobile());
        dept.setEmail(dto.getEmail());
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : "1");
        baseMapper.updateById(dept);
        return CommonResult.success();
    }

    @Override
    public CommonResult<Void> delete(Long id) {
        if (id == null) {
            return CommonResult.failed("部门ID不能为空");
        }

        // 检查是否有子部门
        LambdaQueryWrapper<SysDeptPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDeptPO::getParentId, id);
        if (baseMapper.selectCount(wrapper) > 0) {
            return CommonResult.failed("该部门下存在子部门，无法删除");
        }

        // 检查是否有关联用户
        // TODO: 检查用户表中是否有引用此部门

        baseMapper.deleteById(id);
        return CommonResult.success();
    }

    @Override
    public CommonResult<List<SysDeptVO>> treeSelect() {
        List<SysDeptPO> allDepts = list();
        List<SysDeptVO> tree = buildTree(allDepts, 0L);
        return CommonResult.success(tree);
    }

    private List<SysDeptVO> buildTree(List<SysDeptPO> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .sorted((a, b) -> {
                    int orderA = a.getOrderNum() != null ? a.getOrderNum() : 0;
                    int orderB = b.getOrderNum() != null ? b.getOrderNum() : 0;
                    return Integer.compare(orderA, orderB);
                })
                .map(dept -> {
                    SysDeptVO vo = convertToVO(dept);
                    List<SysDeptVO> children = buildTree(allDepts, dept.getId());
                    vo.setChildren(children.isEmpty() ? null : children);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private SysDeptVO convertToVO(SysDeptPO dept) {
        SysDeptVO vo = new SysDeptVO();
        vo.setId(dept.getId());
        vo.setParentId(dept.getParentId());
        vo.setName(dept.getName());
        vo.setOrderNum(dept.getOrderNum());
        vo.setLeader(dept.getLeader());
        vo.setMobile(dept.getMobile());
        vo.setEmail(dept.getEmail());
        vo.setStatus(dept.getStatus());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
}
