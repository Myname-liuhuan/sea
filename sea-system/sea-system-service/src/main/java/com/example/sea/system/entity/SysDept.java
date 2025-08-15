package com.example.sea.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sea.common.mybatis.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 部门表实体类
 * @author admin
 * @date 2025-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {


    /**
     * 父部门ID
     */
    private  Long  parentId;

    /**
     * 部门名称
     */
    private  String  name;

    /**
     * 显示顺序
     */
    private  Integer  orderNum;

    /**
     * 负责人姓名
     */
    private  String  leader;

    /**
     * 联系电话
     */
    private  String  mobile;

    /**
     * 邮箱
     */
    private  String  email;

    /**
     * 部门状态 0停用 1正常
     */
    private  String  status;
}
