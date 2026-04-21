package com.example.sea.system.api.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 部门显示对象
 * @author admin
 * @date 2025-08-14
 */
@Data
public class SysDeptVO {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 负责人姓名
     */
    private String leader;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态 0停用 1正常
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子部门
     */
    private List<SysDeptVO> children;
}
