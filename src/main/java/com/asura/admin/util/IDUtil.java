package com.asura.admin.util;

import com.asura.admin.util.text.UUID;

/**
 * @Author Rhett
 * @Date 2021/8/7
 * @Description ID生成器工具类
 */
public class IDUtil {
    /**
     * 获取随机UUID
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取简化的UUID，去掉了"-"
     * @return 去掉了中间连接符的简化UUID
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }
}
