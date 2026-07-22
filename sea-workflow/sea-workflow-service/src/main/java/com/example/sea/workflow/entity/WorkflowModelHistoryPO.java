package com.example.sea.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 流程模型历史版本实体。每次保存 BPMN 自动落一条（version 单调递增）。
 *
 * <p>设计依据：
 * - 现行 BPMN 仍在 ACT_RE_MODEL.EDITOR_SOURCE_ 中，这里只做"过去版本"快照
 * - 一份 model_id 对应多条 history 记录（按 version 排序）
 * - 业务侧的"回滚"就是把某个历史 version 的 bpmn_xml 重新写回 ACT_RE_MODEL
 *
 * @author liuhuan
 * @date 2026-07-19
 */
@Data
@Accessors(chain = true)
@TableName("wf_workflow_model_history")
public class WorkflowModelHistoryPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** ACT_RE_MODEL.ID_ */
    private String modelId;

    /** 该模型的历史版本号（单调递增，从 1 开始） */
    private Integer version;

    /** 历史 BPMN XML 全文 */
    private String bpmnXml;

    /** 历史 SVG 预览（可选） */
    private String svg;

    /** 本次保存说明 */
    private String changeComment;

    private Long creatorId;
    private String creatorName;

    private LocalDateTime createTime;

    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}