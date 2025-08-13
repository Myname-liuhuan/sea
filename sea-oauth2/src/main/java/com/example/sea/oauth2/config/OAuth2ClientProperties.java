package com.example.sea.oauth2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "oauth2.client")
@Data
public class OAuth2ClientProperties {

    private String id;

    private String secret;
    
    private String redirectUri;
}
