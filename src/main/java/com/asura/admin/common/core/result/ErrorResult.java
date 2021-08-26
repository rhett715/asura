package com.asura.admin.common.core.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Rhett
 * @Date 2021/7/24
 * @Description
 */
@Data
@ApiModel(value = "ErrorResult", description = " 异常错误的返回信息实体")
public class ErrorResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "错误编码")
    private Integer code;

    @ApiModelProperty(value = "消息描述")
    private String msg;

    @ApiModelProperty(value = "异常")
    private String exception;

    public static ErrorResult fail(ResultCode resultCode, Throwable e, String message) {
        ErrorResult errorResult = ErrorResult.fail(resultCode, e);
        errorResult.setMsg(message);
        return errorResult;
    }
    public static ErrorResult fail(ResultCode resultCode, Throwable e) {
        ErrorResult errorResult = new ErrorResult();
        errorResult.setCode(resultCode.getCode());
        errorResult.setMsg(resultCode.getMsg());
        errorResult.setException(e.getClass().getName());
        return errorResult;
    }
    public static ErrorResult fail(Integer code, String message) {
        ErrorResult errorResult = new ErrorResult();
        errorResult.setCode(code);
        errorResult.setMsg(message);
        return errorResult;
    }
}
