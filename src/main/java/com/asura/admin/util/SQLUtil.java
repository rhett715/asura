package com.asura.admin.util;

import com.asura.admin.exception.BusinessException;

/**
 * @Author Rhett
 * @Date 2021/8/7
 * @Description SQL操作工具类
 */
public class SQLUtil {

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 检查字符，防止注入绕过
     * @param value 查询字符串
     * @return String
     */
    public static String escapeOrderBySQL(String value) {
        if (StringUtil.isNotEmpty(value) && !isValidOrderBySQL(value)) {
            throw new BusinessException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * 验证order by语法是否符合规范
     * @param value 查询字符串
     * @return 是否符合规范
     */
    public static boolean isValidOrderBySQL(String value) {
        return value.matches(SQL_PATTERN);
    }
}
