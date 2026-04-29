package com.example.sea.common.core.result;

import java.util.List;

import lombok.Data;

/**
 * 分页结果封装
 * @author liuhuan
 * @date 2026-04-24
 */
@Data
public class PageResult<T> {

    private List<T> rows;

    private Long total;

    private Long pageNum;

    private Long pageSize;

    public PageResult() {}

    public PageResult(List<T> rows, Long total, Long pageNum, Long pageSize) {
        this.rows = rows;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(List<T> rows, Long total, Long pageNum, Long pageSize) {
        return new PageResult<>(rows, total, pageNum, pageSize);
    }
}