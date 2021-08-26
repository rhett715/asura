package com.asura.admin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description token 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    /**
     * 令牌秘钥
     */
    private String secret;

    /**
     * 令牌有效期（默认30分钟）
     */
    private int expireTime;
}
