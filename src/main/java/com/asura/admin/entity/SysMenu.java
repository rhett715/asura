package com.asura.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.*;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysMenu对象", description="菜单权限表")
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    @ApiModelProperty(value = "路由地址")
    private String path;

    @Size(max = 200, message = "组件路径不能超过255个字符")
    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "是否为外链（0是 1否）")
    private String isFrame;

    @ApiModelProperty(value = "是否缓存（0缓存 1不缓存）")
    private String isCache;

    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty(value = "菜单状态（0显示 1隐藏）")
    private String visible;

    @ApiModelProperty(value = "菜单状态（0正常 1停用）")
    private String status;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

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
    @ApiModelProperty(value = "父菜单名称")
    private String parentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "子菜单")
    private List<SysMenu> children = new ArrayList<>();
}
