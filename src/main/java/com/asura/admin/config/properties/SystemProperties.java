package com.asura.admin.config.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 读取项目相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "asura")
public class SystemProperties {
    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /** 实例演示开关 */
    private boolean demoEnabled;

    /** 获取地址开关 */
    @Getter
    private static boolean addressEnabled;

    public void setAddressEnabled(boolean addressEnabled) {
        SystemProperties.addressEnabled = addressEnabled;
    }
}
