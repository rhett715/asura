package com.asura.admin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 重复提交 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "repeat-submit")
public class RepeatSubmitProperties {
    /**
     * 间隔时间(毫秒)
     */
    private int intervalTime;
}
