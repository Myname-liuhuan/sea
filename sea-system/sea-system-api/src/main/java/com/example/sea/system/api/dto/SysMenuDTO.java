package com.example.sea.system.api.dto;

import com.example.sea.common.core.entity.dto.BaseDTO;
import com.example.sea.common.core.validation.GroupInsert;
import com.example.sea.common.core.validation.GroupUpdate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单数据传输对象
 * @author liuhuan
 * @date 2025-10-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuDTO extends BaseDTO  {

    /*** 主键id */
    @NotNull(message = "id不能为空", groups = {GroupUpdate.class})
    private Long id;
    
    /**
     * 父菜单ID
     */
    private  Long  parentId;

    /**
     * 菜单名称
     */
    @NotNull(message = "菜单名称不能为空", groups = {GroupInsert.class})
    private  String  menuName;

    /**
     * 类型 1目录 2菜单 3按钮
     */
    @NotNull(message = "菜单类型不能为空", groups = {GroupInsert.class})
    private  String  menuType;

    /**
     * 显示顺序
     */
    private  Integer  orderNum;

    /**
     * 路由地址
     */
    private  String  path;

    /**
     * 组件路径
     */
    private  String  component;

    /**
     * 权限标识 sys:user:add
     */
    private  String  perms;

    /**
     * 菜单图标
     */
    private  String  icon;

    /**
     * 侧边栏是否显示 0隐藏 1显示
     * 一般情况下编辑页面应该是0表示隐藏侧边栏
     */
    private  String  visible;

    /**
     * 状态 0停用 1正常
     */
    private  String  status;
}
