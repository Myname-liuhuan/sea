package com.example.sea.system.interfaces.dto;

import java.util.List;

import com.example.sea.common.core.entity.dto.BaseDTO;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色数据传输对象
 * @author admin
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleDTO extends BaseDTO {

     /*** 主键id */
    @NotNull(message = "id不能为空", groups = {GroupUpdate.class})
    private Long id;

    /**
     * 角色名称
     */
    @NotNull(message = "角色名称不能为空", groups = {GroupInsert.class})
    private  String  roleName;

    /**
     * 角色编码(唯一)
     */
    @NotNull(message = "角色编码不能为空", groups = {GroupInsert.class})
    private  String  roleCode;

    /**
     * 角色描述
     */
    private  String  roleDesc;

    /**
     * 数据范围 1全部 2本部门及以下 3本部门 4本人
     */
    private  String  dataScope;

    /**
     * 状态（1正常 0停用）
     */
    private  String  status;

    /**
     * 该角色下用户ID集合
     */
    private List<Long> userIdList;

    
}
