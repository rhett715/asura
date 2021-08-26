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
 * 操作日志记录
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
@ApiModel(value="SysOperationLog对象", description="操作日志记录")
public class SysOperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "操作序号")
    @ApiModelProperty(value = "日志主键")
    @TableId(value = "opt_id", type = IdType.AUTO)
    private Long optId;

    @ExcelProperty(value = "操作模块标题")
    @ApiModelProperty(value = "模块标题")
    private String title;

    @ExcelProperty(value = "业务类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_opt_type")
    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    private Integer businessType;

    @ExcelProperty(value = "请求方法名称")
    @ApiModelProperty(value = "方法名称")
    private String method;

    @ExcelProperty(value = "请求方式")
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    @ExcelProperty(value = "操作类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    @ApiModelProperty(value = "操作人类别（0其它 1后台用户 2手机端用户）")
    private Integer operatorType;

    @ExcelProperty(value = "操作人员")
    @ApiModelProperty(value = "操作人员")
    private String optName;

    @ExcelProperty(value = "部门名称")
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ExcelProperty(value = "请求地址")
    @ApiModelProperty(value = "请求URL")
    private String optUrl;

    @ExcelProperty(value = "操作地址")
    @ApiModelProperty(value = "主机地址")
    private String optIp;

    @ExcelProperty(value = "操作地点")
    @ApiModelProperty(value = "操作地点")
    private String optLocation;

    @ExcelProperty(value = "请求参数")
    @ApiModelProperty(value = "请求参数")
    private String optParam;

    @ExcelProperty(value = "返回参数")
    @ApiModelProperty(value = "返回参数")
    private String jsonResult;

    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_common_status")
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    private Integer status;

    @ExcelProperty(value = "错误消息")
    @ApiModelProperty(value = "错误消息")
    private String errorMsg;

    @ExcelProperty(value = "操作时间")
    @ApiModelProperty(value = "操作时间")
    private Date optTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型数组")
    private Integer[] businessTypes;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
