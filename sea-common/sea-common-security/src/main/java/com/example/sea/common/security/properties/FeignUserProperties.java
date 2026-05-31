package com.example.sea.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Feign请求专用账号配置
 * 用于服务间Feign调用时的身份认证
 * @author liuhuan
 * @date 2026-05-30
 */
@Data
@Component
@ConfigurationProperties(prefix = "security.feign")
public class FeignUserProperties {

    /**
     * Feign调用专用用户名
     */
    private String username = "feign_user";

    /**
     * Feign调用专用密码
     */
    private String password = "feign123456";

}