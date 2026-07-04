package com.example.sea.workflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.workflow.entity.WorkflowTaskPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单主表 Mapper。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Mapper
public interface WorkflowTaskMapper extends BaseMapper<WorkflowTaskPO> {

}
