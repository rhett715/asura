package com.asura.admin.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description swagger 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    /**
     * 是否开启swagger
     */
    private Boolean enabled;

    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 版本
     */
    private String version;

    /**
     * 作者信息
     */
    private Contact contact;

    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * 联系人
         **/
        private String name;
        /**
         * 联系人url
         **/
        private String url;
        /**
         * 联系人email
         **/
        private String email;
    }
}
