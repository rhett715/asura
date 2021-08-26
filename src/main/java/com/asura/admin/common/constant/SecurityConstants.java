package com.asura.admin.common.constant;

import io.jsonwebtoken.Claims;

/**
 * @Author Rhett
 * @Date 2021/8/8
 * @Description Security相关常量
 */
public class SecurityConstants {

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌自定义标识
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * Swagger白名单
     */
    public static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/webjars/**",
            //knife4j
            "/doc.html",
    };

    /**
     * 其他白名单
     */
    public static final String[] URL_WHITELIST = {
            "/login",
            "/register",
            "/captchaImage",
            // Spring Boot Actuator 的安全配置
            "/actuator",
            "/actuator/**",
            "/favicon.ico",
    };
}
