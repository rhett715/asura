package com.asura.admin.config;

import com.asura.admin.common.filter.RepeatableFilter;
import com.asura.admin.common.filter.XssFilter;
import com.asura.admin.config.properties.XssProperties;
import com.asura.admin.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Rhett
 * @Date 2021/8/24
 * @Description Filter配置
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Configuration
@ConditionalOnProperty(value = "xss.enabled", havingValue = "true")
public class FilterConfig {
    @Autowired
    private XssProperties xssProperties;

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(StringUtil.split(xssProperties.getUrlPatterns(), ","));
        registration.setName("xssFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", xssProperties.getExcludes());
        registration.setInitParameters(initParameters);
        return registration;
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}
