package com.asura.admin.security.handler;

import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.util.JacksonUtils;
import com.asura.admin.util.ServletUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Rhett
 * @Date 2021/8/22
 * @Description 认证过的用户访问无权限资源时的处理方式
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    /**
     * 当用户尝试访问需要权限才能的REST资源而权限不足的时候,
     * 将调用此方法发送403响应以及错误信息
     * @param request 请求
     * @param response 响应
     * @param accessDeniedException 权限不足异常
     * @throws IOException IO异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        accessDeniedException = new AccessDeniedException("Sorry you don not enough permissions to access it!");
        ServletUtils.renderString(response, JacksonUtils.toJsonString(JsonResult.fail(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage())));
    }
}
