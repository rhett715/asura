package com.asura.admin.common.interceptor;

import com.asura.admin.common.annotation.RepeatSubmit;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.util.JacksonUtils;
import com.asura.admin.util.ServletUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description 防止重复提交拦截器
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(annotation, request)) {
                    JsonResult<String> ajaxResult = JsonResult.fail("不允许重复提交，请稍后再试");
                    ServletUtils.renderString(response, JacksonUtils.toJsonString(ajaxResult));
                    return false;
                }
            }
            return true;
        } else {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     * @param request 请求
     * @return boolean
     */
    public abstract boolean isRepeatSubmit(RepeatSubmit annotation, HttpServletRequest request);
}
