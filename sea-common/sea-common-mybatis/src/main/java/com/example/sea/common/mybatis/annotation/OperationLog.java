package com.example.sea.common.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标注在方法上，用于记录操作日志
 * @author liuhuan
 * @date 2026-05-18
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作标题
     */
    String title();

    /**
     * 业务类型
     */
    String businessType() default "";

    /**
     * 操作类型（0=其它，1=后台用户，2=手机端用户）
     */
    int operatorType() default 1;

    /**
     * 是否异步记录
     */
    boolean async() default false;

}