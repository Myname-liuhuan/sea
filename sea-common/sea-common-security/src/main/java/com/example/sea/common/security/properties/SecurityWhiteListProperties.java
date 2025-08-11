package com.example.sea.common.security.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 请求白名单参数
 * @author liuhuan
 * @date 2025/05/29
 */
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityWhiteListProperties {
    
    private List<String> whitelist;

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }
}
