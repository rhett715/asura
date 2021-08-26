package com.asura.admin.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Rhett
 * @Date 2021/8/20
 * @Description
 */
@Data
@ApiModel(value = "LoginBody", description = "用户登录对象")
public class LoginBody implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "唯一标识")
    private String uuid = "";
}
