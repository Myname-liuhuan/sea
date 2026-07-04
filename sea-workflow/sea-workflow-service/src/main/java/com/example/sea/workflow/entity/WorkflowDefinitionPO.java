package com.example.sea.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 流程定义元数据。
 *
 * <p>BPMN 流程图本体由 Flowable 引擎按 ACT_RE_PROCDEF 维护；本表存储
 * 业务侧的版本标记和审批人解析器 JSON 配置。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("workflow_definition")
public class WorkflowDefinitionPO extends BaseEntity {

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 业务类型 */
    private String businessType;

    /** 版本号 */
    private Integer version;

    /** 节点 / 条件 / 审批人解析器键 JSON */
    private String definitionJson;

    /** 0 停用 1 启用 */
    private Integer enabled;

    /** 当前启用版本 0 否 1 是 */
    private Integer isCurrent;
}
