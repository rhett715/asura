package com.asura.admin.util;

import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.entity.SysDictData;

import java.util.Collection;
import java.util.List;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 字典工具类
 */
public class DictUtils {
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 设置字典缓存
     * @param key 参数键
     * @param dictDataList 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDataList) {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDataList);
    }

    /**
     * 获取字典缓存
     * @param key 参数键
     * @return dictDataList 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtil.isNotNull(cacheObj)) {
            return (List<SysDictData>) cacheObj;
        }
        return null;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel) {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> dataList = getDictCache(dictType);
        if (dataList == null) {
            return null;
        }
        if (StringUtil.containsAny(dictValue, separator) && StringUtil.isNotEmpty(dataList)) {
            for (SysDictData dict : dataList) {
                for (String value : dictValue.split(separator)) {
                    if (value.equals(dict.getDictValue())) {
                        propertyString.append(dict.getDictLabel()).append(separator);
                        break;
                    }
                }
            }
        } else {
            for (SysDictData dict : dataList) {
                if (dictValue.equals(dict.getDictValue())) {
                    return dict.getDictLabel();
                }
            }
        }
        return StringUtil.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator) {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictData> dataList = getDictCache(dictType);
        if (dataList == null) {
            return null;
        }
        if (StringUtil.containsAny(dictLabel, separator) && StringUtil.isNotEmpty(dataList)) {
            for (SysDictData dict : dataList) {
                for (String label : dictLabel.split(separator)) {
                    if (label.equals(dict.getDictLabel())) {
                        propertyString.append(dict.getDictValue()).append(separator);
                        break;
                    }
                }
            }
        } else {
            for (SysDictData dict : dataList) {
                if (dictLabel.equals(dict.getDictLabel())) {
                    return dict.getDictValue();
                }
            }
        }
        return StringUtil.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 删除指定字典缓存
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(RedisConstants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return RedisConstants.SYS_DICT_KEY + configKey;
    }
}
