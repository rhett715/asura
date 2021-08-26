package com.asura.admin.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description
 */
@ApiModel(value = "TreeEntity", description = "Tree基类")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TreeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "父菜单名称")
    private String parentName;

    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "祖级列表")
    private String ancestors;

    @ApiModelProperty(value = "子部门")
    private List<?> children = new ArrayList<>();
}
