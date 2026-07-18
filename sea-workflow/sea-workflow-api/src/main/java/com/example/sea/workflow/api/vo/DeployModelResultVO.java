package com.example.sea.workflow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 部署结果视图。
 *
 * @author liuhuan
 * @date 2026-07-17
 */
@Data
@Schema(description = "模型部署结果")
public class DeployModelResultVO {

    @Schema(description = "Flowable 部署 ID")
    private String deploymentId;

    @Schema(description = "本次部署生成的流程定义 ID 列表")
    private List<String> deployedProcessDefinitionIds;

    @Schema(description = "部署后的 PROCDEF 版本号")
    private Integer version;
}