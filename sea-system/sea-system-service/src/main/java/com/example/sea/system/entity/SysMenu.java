package com.example.sea.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 菜单权限表实体类
 * @author admin
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu")
public class SysMenu  extends BaseEntity {


    /**
     * 父菜单ID
     */
    private  Long  parentId;

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

    /**
     * 状态 0停用 1正常
     */
    private  String  status;
}
