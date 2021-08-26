package com.asura.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysUser对象", description="用户信息表")
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户类型（00系统用户）")
    private String userType;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;

    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private String sex;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    @ApiModelProperty(value = "最后登录时间")
    private Date loginDate;

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

    @JsonIgnore
    @JsonProperty
    public String getPassword() {
        return password;
    }

    @ApiModelProperty(value = "请求参数")
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    @ApiModelProperty(value = "部门对象")
    @TableField(exist = false)
    private SysDept dept;

    @ApiModelProperty(value = "角色对象")
    @TableField(exist = false)
    private List<SysRole> roles;

    @ApiModelProperty(value = "角色组")
    @TableField(exist = false)
    private Long[] roleIds;

    @ApiModelProperty(value = "岗位组")
    @TableField(exist = false)
    private Long[] postIds;

    @ApiModelProperty(value = "角色ID")
    @TableField(exist = false)
    private Long roleId;

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }
}
