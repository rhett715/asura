package com.asura.admin.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.asura.admin.common.annotation.ExcelDictFormat;
import com.asura.admin.common.convert.ExcelDictConvert;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>
 * 角色信息表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated //忽略未注解
@ApiModel(value="SysRole对象", description="角色信息表")
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "角色序号")
    @ApiModelProperty(value = "角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @ExcelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ExcelProperty(value = "角色权限")
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    @ApiModelProperty(value = "角色权限字符串")
    private String roleKey;

    @ExcelProperty(value = "角色排序")
    @NotBlank(message = "显示顺序不能为空")
    @ApiModelProperty(value = "显示顺序")
    private Integer roleSort;

    @ExcelProperty(value = "数据范围", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
    @ApiModelProperty(value = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）")
    private String dataScope;

    @ApiModelProperty(value = "菜单树选择项是否关联显示")
    private Boolean menuCheckStrictly;

    @ApiModelProperty(value = "部门树选择项是否关联显示")
    private Boolean deptCheckStrictly;

    @ExcelProperty(value = "角色状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    @ApiModelProperty(value = "角色状态（0正常 1停用）")
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

    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "用户是否存在此角色标识 默认不存在")
    private boolean flag = false;

    @TableField(exist = false)
    @ApiModelProperty(value = "菜单组")
    private Long[] menuIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门组（数据权限）")
    private Long[] deptIds;

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }
}
