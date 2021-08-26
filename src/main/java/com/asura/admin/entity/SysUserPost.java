package com.asura.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户与岗位关联表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysUserPost对象", description="用户与岗位关联表")
public class SysUserPost implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "岗位ID")
    private Long postId;


}
