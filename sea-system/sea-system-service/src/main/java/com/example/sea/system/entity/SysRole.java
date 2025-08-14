package com.example.sea.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.core.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 角色表实体类
 * @author admin
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private  String  roleName;

    /**
     * 角色编码(唯一)
     */
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

    
}
