package com.asura.admin.security.handler;

import com.asura.admin.common.constant.Constants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.service.SystemLogService;
import com.asura.admin.util.JacksonUtils;
import com.asura.admin.util.ServletUtils;
import com.asura.admin.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description 自定义退出处理类 返回成功
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SystemLogService systemLogService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtil.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            systemLogService.recordLoginInfo(userName, Constants.LOGOUT, "退出成功", request);
        }
        ServletUtils.renderString(response, JacksonUtils.toJsonString(JsonResult.fail(HttpServletResponse.SC_OK, "退出成功")));
    }
}
