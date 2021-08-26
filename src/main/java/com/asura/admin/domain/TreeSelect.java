package com.asura.admin.domain;

import com.asura.admin.entity.SysDept;
import com.asura.admin.entity.SysMenu;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Rhett
 * @Date 2021/8/19
 * @Description
 */
@Data
@NoArgsConstructor
@ApiModel(value = "TreeSelect", description = "树结构实体类")
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点ID")
    private Long id;

    @ApiModelProperty(value = "节点名称")
    private String label;

    @ApiModelProperty(value = "子节点")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect(SysDept dept) {
        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenu menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }
}
