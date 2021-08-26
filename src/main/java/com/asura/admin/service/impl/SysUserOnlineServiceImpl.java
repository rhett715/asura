package com.asura.admin.service.impl;

import com.asura.admin.entity.SysUserOnline;
import com.asura.admin.security.LoginUser;
import com.asura.admin.service.SysUserOnlineService;
import com.asura.admin.util.StringUtil;
import org.springframework.stereotype.Service;

/**
 * @Author Rhett
 * @Date 2021/8/19
 * @Description 在线用户 服务层处理
 */
@Service
public class SysUserOnlineServiceImpl implements SysUserOnlineService {

    /**
     * 通过登录地址查询信息
     * @param ipAddr 登录地址
     * @param user   用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByIpAddr(String ipAddr, LoginUser user) {
        if (StringUtil.equals(ipAddr, user.getIpAddr())) {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user) {
        if (StringUtil.equals(userName, user.getUsername())) {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     * @param ipAddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String ipAddr, String userName, LoginUser user) {
        if (StringUtil.equals(ipAddr, user.getIpAddr()) && StringUtil.equals(userName, user.getUsername())) {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public SysUserOnline loginUserToUserOnline(LoginUser user) {
        if (StringUtil.isNull(user) || StringUtil.isNull(user.getUser())) {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpAddr(user.getIpAddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        if (StringUtil.isNotNull(user.getUser().getDept())) {
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        return sysUserOnline;
    }
}
