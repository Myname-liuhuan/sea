package com.example.sea.workflow.delegate;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 密码生成器单测。
 *
 * <p>仅验证 {@code PasswordResetDelegate.randomPassword}：
 * <ul>
 *   <li>长度正确</li>
 *   <li>仅用 Base62 表</li>
 *   <li>每次不同（随机性）</li>
 *   <li>不出现易混字符 0 / O / 1 / l</li>
 * </ul>
 */
class PasswordResetDelegateTest {

    private static final String BASE62 = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    @Test
    void password_lengthAndCharset() {
        for (int i = 0; i < 50; i++) {
            char[] p = PasswordResetDelegate.randomPassword(8);
            assertEquals(8, p.length);
            for (char c : p) {
                assertTrue(BASE62.indexOf(c) >= 0, "字符越界: " + c);
                // 易混字符全部剔除
                assertNotEquals('0', c, "不应出现 0");
                assertNotEquals('O', c, "不应出现 O");
                assertNotEquals('1', c, "不应出现 1");
                assertNotEquals('l', c, "不应出现 l");
            }
        }
    }

    @Test
    void password_isRandomAcrossRuns() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            seen.add(new String(PasswordResetDelegate.randomPassword(8)));
        }
        // 200 次内期望至少 195+ 不重复
        assertTrue(seen.size() >= 195, "随机性不足 unique=" + seen.size());
    }
}