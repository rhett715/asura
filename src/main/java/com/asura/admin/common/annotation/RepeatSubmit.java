package com.asura.admin.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解防止表单重复提交
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    /**
     * 默认使用全局配置
     */
    int intervalTime() default 0;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
