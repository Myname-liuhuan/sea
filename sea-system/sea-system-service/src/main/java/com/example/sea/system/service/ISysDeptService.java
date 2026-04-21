package com.example.sea.system.service;

import java.util.List;

import com.example.sea.common.core.result.CommonResult;
import com.example.sea.system.api.dto.SysDeptDTO;
import com.example.sea.system.api.vo.SysDeptVO;
import com.example.sea.system.entity.SysDeptPO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 部门表服务接口
 * @author admin
 * @date 2025-08-14
 */
public interface ISysDeptService extends IService<SysDeptPO> {

    /**
     * 获取部门树
     * @return 部门树列表
     */
    CommonResult<List<SysDeptVO>> tree();

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门信息
     */
    CommonResult<SysDeptVO> getById(Long id);

    /**
     * 新增部门
     * @param dto 部门信息
     * @return 操作结果
     */
    CommonResult<Void> add(SysDeptDTO dto);

    /**
     * 更新部门
     * @param dto 部门信息
     * @return 操作结果
     */
    CommonResult<Void> update(SysDeptDTO dto);

    /**
     * 删除部门
     * @param id 部门ID
     * @return 操作结果
     */
    CommonResult<Void> delete(Long id);

    /**
     * 获取部门下拉树
     * @return 部门下拉树列表
     */
    CommonResult<List<SysDeptVO>> treeSelect();
}
