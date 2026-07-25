package com.example.sea.system.api.constants;

public interface PermissionConstants {

    // 用户管理
    String SYS_USER_ADD = "sys:user:add";
    String SYS_USER_EDIT = "sys:user:edit";
    String SYS_USER_DELETE = "sys:user:delete";
    String SYS_USER_LIST = "sys:user:list";

    /**
     * 内部服务间回调权限（仅 feign.internal.token / 服务间调用持有）。
     * 持有此权限可访问 SysUserWorkflowCallbackController 下的所有端点。
     * 业务用户（包含 admin）默认无此权限，避免内部接口被外部滥用。
     */
    String INTERNAL_CALLBACK = "internal:callback";

    // 角色管理
    String SYS_ROLE_ADD = "sys:role:add";
    String SYS_ROLE_EDIT = "sys:role:edit";
    String SYS_ROLE_DELETE = "sys:role:delete";
    String SYS_ROLE_LIST = "sys:role:list";

    // 菜单管理
    String SYS_MENU_ADD = "sys:menu:add";
    String SYS_MENU_EDIT = "sys:menu:edit";
    String SYS_MENU_DELETE = "sys:menu:delete";
    String SYS_MENU_LIST = "sys:menu:list";

    // 部门管理
    String SYS_DEPT_ADD = "sys:dept:add";
    String SYS_DEPT_EDIT = "sys:dept:edit";
    String SYS_DEPT_DELETE = "sys:dept:delete";
    String SYS_DEPT_LIST = "sys:dept:list";
}