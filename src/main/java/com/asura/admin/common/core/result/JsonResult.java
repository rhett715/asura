package com.asura.admin.common.core.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author Rhett
 * @Date 2021/8/11
 * @Description
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "JsonResult<T>", description = "正常操作消息响应结果")
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息状态码")
    private Integer code;

    @ApiModelProperty("消息内容")
    private String msg;

    @ApiModelProperty("数据对象")
    private T data;

    private JsonResult() {}

    /**
     * 返回成功消息
     * @param <T> 类型
     * @return 成功消息
     */
    public static <T> JsonResult<T> success() {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setResultCode(ResultCode.SUCCESS);
        return jsonResult;
    }

    /**
     * 返回带数据的成功消息
     * @param data 数据
     * @param <T> 数据的类型
     * @return 成功消息
     */
    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setResultCode(ResultCode.SUCCESS);
        jsonResult.setData(data);
        return jsonResult;
    }

    /**
     * 返回错误消息
     * @param code 消息状态码
     * @param msg 消息内容
     * @param <T> 类型
     * @return 错误消息
     */
    public static <T> JsonResult<T> fail(Integer code, String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setCode(code);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    /**
     * 返回错误消息
     * @param msg 消息内容
     * @param <T> 类型
     * @return 错误消息
     */
    public static <T> JsonResult<T> fail(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setCode(ResultCode.FAILURE.getCode());
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    /**
     * 返回错误消息
     * @param resultCode
     * @param <T> 类型
     * @return 错误消息
     */
    public static <T> JsonResult<T> fail(ResultCode resultCode) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setResultCode(resultCode);
        return jsonResult;
    }


    private void setResultCode(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }
}
