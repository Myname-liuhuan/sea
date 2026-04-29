package com.example.sea.common.mybatis.constants;

import lombok.Getter;

/**
 * 删除标识枚举
 * @author liuhuan
 * @date 2026/4/29
 */
@Getter
public enum DeletedEnum {
    NORMAL(0, "正常"),
    DELETED(1, "删除");

    private final Integer code;

    private final String display;

    DeletedEnum(Integer code, String display) {
        this.code = code;
        this.display = display;
    }
}
