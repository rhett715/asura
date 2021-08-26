package com.asura.admin.service;

import com.asura.admin.domain.RegisterBody;

public interface SysUserAuthService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    String login(String username, String password, String code, String uuid);

    /**
     * 用户注册
     * @param registerBody 用户注册对象
     * @return 结果
     */
    String register(RegisterBody registerBody);

    /**
     * 校验验证码
     * @param username 用户名
     * @param code 密码
     * @param uuid 唯一标识
     */
    void validateCaptcha(String username, String code, String uuid);
}
