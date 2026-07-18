package com.example.sea.workflow.api.constants;

/**
 * 工作流域权限点常量。
 *
 * <p>注册到 sys_menu.perms 后，前端通过 v-hasPermi 控制按钮与菜单显示，
 * 后端通过 @PreAuthorize("hasAuthority('...')") 控制接口访问。
 */
public interface WorkflowPermissionConstants {

    /** 个人级 - 查看"我的申请"列表 */
    String WORKFLOW_MY      = "workflow:my";

    /** 申请重置按钮（用户列表页操作列），所有登录用户均拥有 */
    String WORKFLOW_APPLY   = "workflow:apply";

    /** 审批操作：approve / reassign / "待我审批"列表 */
    String WORKFLOW_APPROVE = "workflow:approve";

    /** 工单监控列表，仅管理员可见 */
    String WORKFLOW_MONITOR = "workflow:monitor";

    // ---- 流程设计器（M5 新增）----

    /** 流程模型 - 查看列表 / 进入设计器 */
    String WORKFLOW_MODEL_READ   = "workflow:model:read";

    /** 流程模型 - 新建 / 编辑元数据 / 保存 BPMN */
    String WORKFLOW_MODEL_WRITE  = "workflow:model:write";

    /** 流程模型 - 部署为流程定义（admin only） */
    String WORKFLOW_MODEL_DEPLOY = "workflow:model:deploy";

    /** 流程模型 - 删除（admin only） */
    String WORKFLOW_MODEL_DELETE = "workflow:model:delete";
}
