package com.asura.admin.common.annotation;

import com.asura.admin.common.enums.BusinessType;
import com.asura.admin.common.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {
    /**
     * 模块
     * @return String
     */
    String title() default "";

    /**
     * 功能
     * @return BusinessType业务操作类型
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类型
     * @return 操作人类型
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     * @return boolean
     */
    boolean isSaveRequestData() default true;
}
