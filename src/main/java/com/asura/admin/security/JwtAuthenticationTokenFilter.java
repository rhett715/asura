package com.asura.admin.security;

import com.asura.admin.common.constant.SecurityConstants;
import com.asura.admin.util.SecurityUtils;
import com.asura.admin.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Rhett
 * @Date 2021/8/20
 * @Description token过滤器 验证token有效性
 */
public class JwtAuthenticationTokenFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private TokenService tokenService;

    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            log.info("OPTIONS请求，放行");
            chain.doFilter(request, response);
            return;
        }
        //TODO: 这里拦截了所有请求
        String tokenHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (StringUtil.isEmpty(tokenHeader) || tokenHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            log.info("非法登录！tokenHeader: " + tokenHeader);
            chain.doFilter(request, response);
            return;
        }
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtil.isNotNull(loginUser) && StringUtil.isNull(SecurityUtils.getAuthentication())) {
            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("JWT过滤器通过校验请求头token自动登录成功, user : {}", loginUser);
        }
        chain.doFilter(request, response);
    }
}
