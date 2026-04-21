package com.example.sea.system.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部门数据传输对象
 * @author admin
 * @date 2025-08-14
 */
@Data
public class SysDeptDTO {

    /**
     * 部门ID（编辑时传入）
     */
    private Long id;

    /**
     * 父部门ID
     */
    @NotNull(message = "父部门不能为空")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
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
}
