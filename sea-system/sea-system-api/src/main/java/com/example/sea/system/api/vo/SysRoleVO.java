package com.example.sea.system.api.vo;

import com.example.sea.common.core.entity.vo.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色视图对象
 * @author admin
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleVO extends BaseVO {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码(唯一)
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 数据范围 1全部 2本部门及以下 3本部门 4本人
     */
    private String dataScope;

    /**
     * 状态（1正常 0停用）
     */
    private String status;
}