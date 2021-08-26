package com.asura.admin.service;

import com.asura.admin.entity.SysUserOnline;
import com.asura.admin.security.LoginUser;

/**
 * 在线用户 服务层
 */
public interface SysUserOnlineService {
    /**
     * 通过登录地址查询信息
     * @param ipAddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    SysUserOnline selectOnlineByIpAddr(String ipAddr, LoginUser user);

    /**
     * 通过用户名称查询信息
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    SysUserOnline selectOnlineByUserName(String userName, LoginUser user);

    /**
     * 通过登录地址/用户名称查询信息
     * @param ipAddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    SysUserOnline selectOnlineByInfo(String ipAddr, String userName, LoginUser user);

    /**
     * 设置在线用户信息
     * @param user 用户信息
     * @return 在线用户
     */
    SysUserOnline loginUserToUserOnline(LoginUser user);
}
