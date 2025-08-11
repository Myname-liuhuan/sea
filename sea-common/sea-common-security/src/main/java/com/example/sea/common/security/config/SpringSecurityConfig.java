package com.example.sea.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.sea.common.security.properties.SecurityWhiteListProperties;

@Configuration
public class SpringSecurityConfig {

    private final SecurityWhiteListProperties securityProperties;

    @Autowired
    public SpringSecurityConfig(SecurityWhiteListProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(securityProperties.getWhitelist().toArray(new String[0])).permitAll()
                .anyExchange().authenticated()
            );
        return http.build();
    }
}