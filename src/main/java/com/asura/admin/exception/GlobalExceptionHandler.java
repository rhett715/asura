package com.asura.admin.exception;

import com.asura.admin.common.core.result.ErrorResult;
import com.asura.admin.common.core.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * @Author Rhett
 * @Date 2021/8/11
 * @Description 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 参数校验不通过
     * @param ex 参数校验异常
     * @return 异常返回信息
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ErrorResult handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException msg:{}", ex.getMessage());
        return ErrorResult.fail(ResultCode.PARAMS_IS_INVALID, ex);
    }

    /**
     * 自定义异常
     * @param request 请求
     * @param ex 自定义异常
     * @return 异常返回信息
     */
    @ExceptionHandler(BusinessException.class)
    public ErrorResult handleBusinessException(HttpServletRequest request, BusinessException ex) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常：{}", requestURI, ex.getMessage());
        return ErrorResult.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 未知异常
     * @param ex 异常
     * @return 异常返回信息
     */
    @ExceptionHandler(Exception.class)
    public ErrorResult handlerException(HttpServletRequest request, Exception ex) {
        String requestURI = request.getRequestURI();
        log.error("请求：{}发生未知异常！原因是:{}", requestURI, ex.getMessage());
        return ErrorResult.fail(ResultCode.RUNTIME_EXCEPTION, ex);
    }
}
