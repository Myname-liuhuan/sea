package com.example.sea.system.api.vo;

import lombok.Data;

/**
 * 菜单选项显示对象，用于下拉选择
 * @author liuhuan
 * @date 2025-10-15
 */
@Data
public class SysMenuOptionVO {

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 类型 1目录 2菜单 3按钮
     */
    private String menuType;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 侧边栏是否显示 0隐藏 1显示
     */
    private String visible;
}
