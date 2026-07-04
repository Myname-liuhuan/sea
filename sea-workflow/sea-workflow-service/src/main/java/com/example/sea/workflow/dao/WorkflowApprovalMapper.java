package com.example.sea.workflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.workflow.entity.WorkflowApprovalPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录 Mapper。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper
public interface WorkflowApprovalMapper extends BaseMapper<WorkflowApprovalPO> {

}
