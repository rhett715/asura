package com.asura.admin.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.*;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysDept对象", description="部门表")
public class SysDept implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门id")
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    @ApiModelProperty(value = "父部门id")
    private Long parentId;

    @ApiModelProperty(value = "祖级列表")
    private String ancestors;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过30个字符")
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "负责人")
    private String leader;

    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    @ApiModelProperty(value = "联系电话")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "部门状态（0正常 1停用）")
    private String status;

    @TableLogic
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "父部门名称")
    private String parentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "子部门")
    private List<SysDept> children = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
