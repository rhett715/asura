package com.asura.admin.exception;

import com.asura.admin.common.core.result.ErrorCode;

/**
 * @Author Rhett
 * @Date 2021/8/11
 * @Description 自定义业务异常
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 异常响应码
     */
    private Integer code;

    /**
     * 异常响应信息
     */
    private String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
