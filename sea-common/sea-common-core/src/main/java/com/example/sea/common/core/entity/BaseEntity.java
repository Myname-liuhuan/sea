package com.example.sea.common.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liuhuan
 * @date 2025/2/22
 */
@Data
public class BaseEntity {

    /** 主键 */
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 创建人 */
    private Long createBy;

    /** 更新人 */
    private Long updateBy;

    /** 删除标志（0启用 1删除）*/
    private Integer delFlag;
}
