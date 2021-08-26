package com.asura.admin.util;

import cn.hutool.extra.servlet.ServletUtil;
import com.asura.admin.util.text.Convert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * @Author Rhett
 * @Date 2021/8/7
 * @Description 客户端工具类
 */
public class ServletUtils extends ServletUtil {

    /**
     * 获取String参数
     * @param name 参数名
     * @return 参数内容
     */
    public static String getParameter(String name) {
        return Objects.requireNonNull(getRequest()).getParameter(name);
    }

    /**
     * 获取String参数
     * @param name 参数名
     * @param defaultValue 参数默认值
     * @return String参数
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(Objects.requireNonNull(getRequest()).getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     * @param name 参数名
     * @return Integer参数值
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(Objects.requireNonNull(getRequest()).getParameter(name));
    }

    /**
     * 获取Integer参数
     * @param name 参数名
     * @param defaultValue 参数默认值
     * @return Integer参数值
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(Objects.requireNonNull(getRequest()).getParameter(name), defaultValue);
    }

    /**
     * 获取请求
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        try {
            return Objects.requireNonNull(getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取响应
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        try {
            return Objects.requireNonNull(getRequestAttributes()).getResponse();
        } catch (Exception e) {
            return null;
        }
    }

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将字符串渲染到客户端
     * @param response 渲染对象
     * @param string 待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) throws IOException {
        //response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(string);

    }

    /**
     * 是否是Ajax异步请求
     * @param request 请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }
        String uri = request.getRequestURI();
        if (StringUtil.inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }
        String ajax = request.getParameter("__ajax");
        return StringUtil.inStringIgnoreCase(ajax, "json", "xml");
    }


    /**
     * 内容编码
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 内容解码
     * @param str 内容
     * @return 解码后的内容
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String getClientIP() {
        return getClientIP(Objects.requireNonNull(getRequest()));
    }
}
