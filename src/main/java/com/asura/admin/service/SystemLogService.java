package com.asura.admin.service;

import com.asura.admin.common.constant.Constants;
import com.asura.admin.entity.SysLoginInfo;
import com.asura.admin.entity.SysOperationLog;
import com.asura.admin.util.IPUtils;
import com.asura.admin.util.ServletUtils;
import com.asura.admin.util.StringUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author Rhett
 * @Date 2021/8/25
 * @Description 记录日志，包括登录和操作
 */
@Slf4j
@Component
public class SystemLogService {
    @Autowired
    private SysLoginInfoService loginInfoService;
    @Autowired
    private SysOperationLogService operationLogService;

    /**
     * 记录登录信息
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     */
    public void recordLoginInfo(final String username, final String status, final String message,
                                HttpServletRequest request, final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        final String ip = ServletUtils.getClientIP(request);

        String address = IPUtils.getRealAddressByIP(ip);
        // 打印信息到日志
        String s = getBlock(ip) +
                address +
                getBlock(username) +
                getBlock(status) +
                getBlock(message);
        log.info(s, args);
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        SysLoginInfo loginInfo = new SysLoginInfo();
        loginInfo.setUserName(username);
        loginInfo.setIpAddr(ip);
        loginInfo.setLoginLocation(address);
        loginInfo.setBrowser(browser);
        loginInfo.setOs(os);
        loginInfo.setMsg(message);
        // 日志状态
        if (StringUtil.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            loginInfo.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            loginInfo.setStatus(Constants.FAIL);
        }
        // 插入数据
        loginInfoService.insertLoginInfo(loginInfo);
    }

    /**
     * 操作日志记录
     * @param operationLog 操作日志信息
     */
    public void recordOperationLog(final SysOperationLog operationLog) {
        // 远程查询操作地点
        operationLog.setOptLocation(IPUtils.getRealAddressByIP(operationLog.getOptIp()));
        operationLog.setOptTime(new Date());
        operationLogService.insertOperationLog(operationLog);
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
