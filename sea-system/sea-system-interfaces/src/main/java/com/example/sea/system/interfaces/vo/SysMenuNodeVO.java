package com.example.sea.system.interfaces.vo;

import java.util.List;

import lombok.Data;

/**
 * 菜单节点显示对象
 * 用于表示菜单的层级结构和相关信息
 * @author liuhuan
 * @date 2025-08-14
 */
@Data
public class SysMenuNodeVO {

    List<SysMenuNodeVO> children;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private  String  menuName;

    /**
     * 类型 1目录 2菜单 3按钮
     */
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
    
}
