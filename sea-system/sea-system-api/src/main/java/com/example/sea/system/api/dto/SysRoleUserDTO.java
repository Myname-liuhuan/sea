package com.example.sea.system.api.dto;

import java.util.List;

import com.example.sea.common.core.validation.GroupUpdate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色用户关系表-通过角色增减所下辖的用户 数据传输对象
 * @author liuhuan
 * @date 2025-08-16
 */
@Data
public class SysRoleUserDTO {

     /*** 主键id */
    @NotNull(message = "roleId不能为空", groups = {GroupUpdate.class})
    private Long roleId;

    /**
     * 该角色下用户ID集合
     */
    private List<Long> userIdList;

    
}
