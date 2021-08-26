package com.asura.admin.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.asura.admin.util.file.FileUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description Excel相关处理
 */
public class ExcelUtil {

    /**
     * 对excel表单默认第一个索引名转换成list（EasyExcel）
     * @param is 输入流
     * @return 转换后集合
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return EasyExcel.read(is).head(clazz).autoCloseStream(false).sheet().doReadSync();
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单（EasyExcel）
     * @param list 导出数据集合
     * @param sheetName 工作表的名称
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        try {
            String filename = encodingFilename(sheetName);
            response.reset();
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            FileUtils.setAttachmentResponseHeader(response, URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            ServletOutputStream os = response.getOutputStream();
            EasyExcel.write(os, clazz)
                    .autoCloseStream(false)
                    // 自动适配
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName).doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    /**
     * 解析导出值 0=男,1=女,2=未知
     * @param propertyValue 参数值
     * @param converterExp 翻译注解
     * @param separator 分隔符
     * @return 解析后值
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtil.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        }
        return StringUtil.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 反向解析值 男=0,女=1,未知=2
     * @param propertyValue 参数值
     * @param converterExp 翻译注解
     * @param separator 分隔符
     * @return 解析后值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StringUtil.containsAny(separator, propertyValue)) {
                for (String value : propertyValue.split(separator)) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0]).append(separator);
                        break;
                    }
                }
            } else {
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        }
        return StringUtil.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 解析字典值
     * @param dictValue 字典值
     * @param dictType 字典类型
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String convertDictByExp(String dictValue, String dictType, String separator) {
        return DictUtils.getDictLabel(dictType, dictValue, separator);
    }

    /**
     * 反向解析值字典值
     * @param dictLabel 字典标签
     * @param dictType 字典类型
     * @param separator 分隔符
     * @return 字典值
     */
    public static String reverseDictByExp(String dictLabel, String dictType, String separator) {
        return DictUtils.getDictValue(dictType, dictLabel, separator);
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename) {
        return IDUtil.fastSimpleUUID() + "_" + filename + ".xlsx";
    }
}
