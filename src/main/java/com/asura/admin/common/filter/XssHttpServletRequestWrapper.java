package com.asura.admin.common.filter;

import com.asura.admin.util.StringUtil;
import com.asura.admin.util.html.EscapeUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description XSS过滤处理
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            for (int i = 0; i < length; i++) {
                // 防xss攻击和过滤前后空格
                escapeValues[i] = EscapeUtil.clean(values[i]).trim();
            }
            return escapeValues;
        }
        return super.getParameterValues(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 非json类型，直接返回
        if (!isJsonRequest()) {
            return super.getInputStream();
        }
        // 为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), String.valueOf(StandardCharsets.UTF_8));
        if (StringUtil.isEmpty(json)) {
            return super.getInputStream();
        }
        // xss过滤
        json = EscapeUtil.clean(json).trim();
        final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read() {
                return bis.read();
            }
        };
    }

    /**
     * 是否是Json请求
     */
    public boolean isJsonRequest() {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
        return StringUtil.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }
}
