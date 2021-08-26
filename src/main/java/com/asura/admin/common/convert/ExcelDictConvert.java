package com.asura.admin.common.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.asura.admin.common.annotation.ExcelDictFormat;
import com.asura.admin.util.ExcelUtil;
import com.asura.admin.util.StringUtil;

import java.lang.reflect.Field;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 字典格式化转换处理
 */
public class ExcelDictConvert implements Converter<Object> {
    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anon = getAnnotation(contentProperty.getField());
        String type = anon.dictType();
        String label = cellData.getStringValue();
        String value;
        if (StringUtil.isBlank(type)) {
            value = ExcelUtil.reverseByExp(label, anon.readConverterExp(), anon.separator());
        } else {
            value = ExcelUtil.reverseDictByExp(label, type, anon.separator());
        }
        return Convert.convert(contentProperty.getField().getType(), value);
    }

    @Override
    public CellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (StringUtil.isNull(object)) {
            return new CellData<>("");
        }
        ExcelDictFormat anon = getAnnotation(contentProperty.getField());
        String type = anon.dictType();
        String value = Convert.toStr(object);
        String label;
        if (StringUtil.isBlank(type)) {
            label = ExcelUtil.convertByExp(value, anon.readConverterExp(), anon.separator());
        } else {
            label = ExcelUtil.convertDictByExp(value, type, anon.separator());
        }
        return new CellData<>(label);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}
