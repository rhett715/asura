package com.asura.admin.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.asura.admin.common.annotation.ExcelDictFormat;
import com.asura.admin.common.convert.ExcelDictConvert;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统访问记录
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
@ApiModel(value="SysLoginInfo对象", description="系统访问记录")
public class SysLoginInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "访问ID")
    @ApiModelProperty(value = "访问ID")
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    @ExcelProperty(value = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ExcelProperty(value = "登录地址")
    @ApiModelProperty(value = "登录IP地址")
    private String ipAddr;

    @ExcelProperty(value = "登录地点")
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @ExcelProperty(value = "浏览器")
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @ExcelProperty(value = "操作系统")
    @ApiModelProperty(value = "操作系统")
    private String os;

    @ExcelProperty(value = "登录状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    @ApiModelProperty(value = "登录状态（0成功 1失败）")
    private String status;

    @ExcelProperty(value = "提示消息")
    @ApiModelProperty(value = "提示消息")
    private String msg;

    @ExcelProperty(value = "访问时间")
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
