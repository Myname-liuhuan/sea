package com.example.sea.common.core.entity.param;

import lombok.Data;

/**
 * 分页查询基础参数
 * @author liuhuan
 * @date 2026-04-24
 */
@Data
public class BaseParam {

    private Long pageNum = 1L;

    private Long pageSize = 10L;
}