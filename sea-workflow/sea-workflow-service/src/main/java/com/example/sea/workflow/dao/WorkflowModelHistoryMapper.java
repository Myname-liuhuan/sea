package com.example.sea.workflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.workflow.entity.WorkflowModelHistoryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 流程模型历史 Mapper。
 *
 * @author liuhuan
 * @date 2026-07-19
 */
@Mapper
public interface WorkflowModelHistoryMapper extends BaseMapper<WorkflowModelHistoryPO> {

    /**
     * 取指定 model 当前最大版本号（用于计算下一次保存的 version）。
     */
    @Select("SELECT IFNULL(MAX(version), 0) FROM wf_workflow_model_history WHERE model_id = #{modelId} AND del_flag = 0")
    int maxVersion(@Param("modelId") String modelId);

    /**
     * 取指定 model 的最新历史版本（按 version 倒序）。
     */
    @Select("SELECT * FROM wf_workflow_model_history WHERE model_id = #{modelId} AND del_flag = 0 ORDER BY version DESC LIMIT 1")
    WorkflowModelHistoryPO selectLatest(@Param("modelId") String modelId);

    /**
     * 取指定 model + version 的历史记录。
     */
    @Select("SELECT * FROM wf_workflow_model_history WHERE model_id = #{modelId} AND version = #{version} AND del_flag = 0 LIMIT 1")
    WorkflowModelHistoryPO selectByVersion(@Param("modelId") String modelId, @Param("version") int version);

    /**
     * 软删除指定 model 的全部历史（仅 admin 工具场景用）。
     */
    @Update("UPDATE wf_workflow_model_history SET del_flag = 1 WHERE model_id = #{modelId}")
    int softDeleteByModel(@Param("modelId") String modelId);
}