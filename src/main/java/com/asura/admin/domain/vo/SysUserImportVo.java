package com.asura.admin.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.asura.admin.common.annotation.ExcelDictFormat;
import com.asura.admin.common.convert.ExcelDictConvert;
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
@ApiModel(value = "SysUserImportVo", description = "用户对象导入VO")
public class SysUserImportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "用户序号")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ExcelProperty(value = "部门编号")
    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ExcelProperty(value = "登录名称")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ExcelProperty(value = "用户名称")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ExcelProperty(value = "用户邮箱")
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ExcelProperty(value = "手机号码")
    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;

    @ExcelProperty(value = "用户性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    @ApiModelProperty(value = "用户性别")
    private String sex;

    @ExcelProperty(value = "帐号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;
}
