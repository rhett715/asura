package com.asura.admin.util;

import com.asura.admin.exception.BusinessException;
import com.asura.admin.security.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author Rhett
 * @Date 2021/8/7
 * @Description 权限相关工具类
 */
public class SecurityUtils {

    /**
     * 用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            throw new BusinessException("获取用户ID异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId() {
        try {
            return getLoginUser().getDeptId();
        } catch (Exception e) {
            throw new BusinessException("获取部门ID异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取用户账号
     */
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new BusinessException("获取用户账号异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取登录用户信息
     * @return 用户信息
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BusinessException("获取登录用户信息异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取 Authentication
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 是否为管理员
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 明文密码加密
     * @param rawPassword 明文密码
     * @return 经过加密后得到的字符串
     */
    public static String encryptPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    /**
     * 判断密码是否相同
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码字符串
     * @return 密码是否相同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String encPassword = encryptPassword("123456");

        System.out.println(encPassword);
        System.out.println(matchesPassword("123456", encPassword));
    }
}
