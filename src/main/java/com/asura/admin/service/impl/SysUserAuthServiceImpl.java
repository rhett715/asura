package com.asura.admin.service.impl;

import com.asura.admin.common.constant.Constants;
import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.constant.SecurityConstants;
import com.asura.admin.common.constant.UserConstants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.domain.RegisterBody;
import com.asura.admin.entity.SysUser;
import com.asura.admin.exception.BusinessException;
import com.asura.admin.security.LoginUser;
import com.asura.admin.security.TokenService;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.service.SysUserAuthService;
import com.asura.admin.service.SysUserService;
import com.asura.admin.service.SystemLogService;
import com.asura.admin.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author Rhett
 * @Date 2021/8/20
 * @Description
 */
@Service
public class SysUserAuthServiceImpl implements SysUserAuthService {
    @Autowired
    private TokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private SystemLogService systemLogService;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    @Override
    public String login(String username, String password, String code, String uuid) {
        HttpServletRequest request = ServletUtils.getRequest();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                systemLogService.recordLoginInfo(username, Constants.LOGIN_FAIL, "用户不存在/密码错误", request);
                throw new BusinessException("用户不存在/密码错误");
            } else {
                systemLogService.recordLoginInfo(username, Constants.LOGIN_FAIL, e.getMessage(), request);
                throw new BusinessException(e.getMessage());
            }
        }
        systemLogService.recordLoginInfo(username, Constants.LOGIN_SUCCESS, "登录成功", request);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUser());
        // 生成token
        String token = tokenService.createToken(loginUser);
        //设置请求头
        Objects.requireNonNull(ServletUtils.getResponse()).setHeader(SecurityConstants.TOKEN_HEADER, token);
        return token;
    }

    /**
     * 用户注册
     * @param registerBody 用户注册对象
     * @return 结果
     */
    @Override
    public String register(RegisterBody registerBody) {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }
        if (StringUtil.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtil.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                systemLogService.recordLoginInfo(username, Constants.REGISTER, "注册成功", Objects.requireNonNull(ServletUtils.getRequest()));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * @param username 用户名
     * @param code 密码
     * @param uuid 唯一标识
     */
    @Override
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = RedisConstants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new BusinessException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new BusinessException("验证码错误");
        }
    }


    /**
     * 记录登录信息
     */
    public void recordLoginInfo(SysUser user) {
        user.setLoginIp(ServletUtils.getClientIP());
        user.setLoginDate(DateUtils.getNowDate());
        user.setUpdateBy(user.getUserName());
        userService.updateUserProfile(user);
    }
}
