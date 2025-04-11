package com.example.sea.code.common.base;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * * 基础VO类
 * * @description 基础VO类
 * * @author liuhuan
 */
@Data
public class BaseVO {
    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
