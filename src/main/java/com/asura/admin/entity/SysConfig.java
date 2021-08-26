package com.asura.admin.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.asura.admin.common.annotation.ExcelDictFormat;
import com.asura.admin.common.convert.ExcelDictConvert;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
@ApiModel(value="SysConfig对象", description="参数配置表")
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "参数主键")
    @ApiModelProperty(value = "参数主键")
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    @ExcelProperty(value = "参数名称")
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    @ApiModelProperty(value = "参数名称")
    private String configName;

    @ExcelProperty(value = "参数键名")
    @NotBlank(message = "参数键名长度不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    @ApiModelProperty(value = "参数键名")
    private String configKey;

    @ExcelProperty(value = "参数键值")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    @ApiModelProperty(value = "参数键值")
    private String configValue;

    @ExcelProperty(value = "系统内置", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    @ApiModelProperty(value = "系统内置（Y是 N否）")
    private String configType;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
