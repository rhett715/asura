package com.asura.admin.common.filter;

import com.asura.admin.util.StringUtil;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description Repeatable 过滤器
 */
public class RepeatableFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest && StringUtil.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            requestWrapper = new RepeatedlyRequestWrapper((HttpServletRequest) request, response);
        }
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {}
}
