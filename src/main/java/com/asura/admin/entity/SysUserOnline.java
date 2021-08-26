package com.asura.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description
 */
@Data
@ApiModel(value = "SysUserOnline", description = "当前在线会话")
public class SysUserOnline implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "会话编号")
    private String tokenId;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "登录IP地址")
    private String ipAddr;

    @ApiModelProperty(value = "登录地址")
    private String loginLocation;

    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "登录时间")
    private Long loginTime;
}
