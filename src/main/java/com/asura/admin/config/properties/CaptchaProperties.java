package com.asura.admin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description 验证码 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 验证码类型
     */
    private String type;

    /**
     * 验证码类别
     */
    private String category;

    /**
     * 数字验证码位数
     */
    private Integer numberLength;

    /**
     * 字符验证码长度
     */
    private Integer charLength;
}
