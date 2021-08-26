package com.asura.admin.security.handler;

import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.util.JacksonUtils;
import com.asura.admin.util.ServletUtils;
import com.asura.admin.util.StringUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description 认证失败处理方式 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -20210809L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        int code = HttpServletResponse.SC_UNAUTHORIZED;
        response.setStatus(code);
        String msg = StringUtil.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtils.renderString(response, JacksonUtils.toJsonString(JsonResult.fail(code, msg)));
    }
}
