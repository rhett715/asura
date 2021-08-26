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
 * 字典类型表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
@ApiModel(value="SysDictType对象", description="字典类型表")
public class SysDictType implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "字典主键")
    @ApiModelProperty(value = "字典主键")
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    @ExcelProperty(value = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ExcelProperty(value = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;

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
