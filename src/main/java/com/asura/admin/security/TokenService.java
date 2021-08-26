package com.asura.admin.security;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.constant.SecurityConstants;
import com.asura.admin.config.properties.TokenProperties;
import com.asura.admin.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description token验证处理
 */
@Component
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TokenProperties tokenProperties;

    /**
     * 从请求中获取用户身份信息
     * @param request 请求
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtil.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(SecurityConstants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                return redisCache.getCacheObject(userKey);
            } catch (Exception e) {
                log.error("从请求中获取用户信息失败：{}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     * @param loginUser 用户信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtil.isNotNull(loginUser) && StringUtil.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtil.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = IDUtil.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);
        //自定义声明
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     * @param loginUser 用户信息
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + tokenProperties.getExpireTime() * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, tokenProperties.getExpireTime(), TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgentUtil.parse(Objects.requireNonNull(ServletUtils.getRequest()).getHeader("User-Agent"));
        String ip = ServletUtils.getClientIP();
        loginUser.setIpAddr(ip);
        loginUser.setLoginLocation(IPUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOs().getName());
    }

    /**
     * 从令牌中获取用户名
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从数据声明生成令牌
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return SecurityConstants.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret()).compact();
    }

    /**
     * 从令牌中获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(tokenProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求token
     * @param request 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (StringUtil.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 获取缓存中token的key
     * @param uuid 唯一标识
     * @return key
     */
    private String getTokenKey(String uuid) {
        return RedisConstants.LOGIN_TOKEN_KEY + uuid;
    }
}
