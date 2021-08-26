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
 * 岗位信息表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
@ApiModel(value="SysPost对象", description="岗位信息表")
public class SysPost implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "岗位序号")
    @ApiModelProperty(value = "岗位ID")
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    @ExcelProperty(value = "岗位编码")
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码长度不能超过64个字符")
    @ApiModelProperty(value = "岗位编码")
    private String postCode;

    @ExcelProperty(value = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过50个字符")
    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ExcelProperty(value = "岗位排序")
    @NotBlank(message = "显示顺序不能为空")
    @ApiModelProperty(value = "显示顺序")
    private Integer postSort;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "用户是否存在此岗位标识 默认不存在")
    private boolean flag = false;
}
