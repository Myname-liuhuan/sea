package com.example.sea.system.constants;

import lombok.Getter;

/**
 * 菜单类型枚举
 * @author liuhuan
 * @date 2025-10-15
 */
@Getter
public enum SysMenuTypeEnum {

    DIRECTORY("1", "目录"),
    MENU("2", "菜单"),
    BUTTON("3", "按钮");

    private final String code;
    private final String display;

    SysMenuTypeEnum(String code, String display) {
        this.code = code;
        this.display = display;
    }
}
