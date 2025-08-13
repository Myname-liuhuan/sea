package com.example.sea.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.sea.common.security.filter.JwtAuthenticationWebFilter;
import com.example.sea.common.security.properties.SecurityWhiteListProperties;

@Configuration
public class SpringSecurityConfig {

    private final SecurityWhiteListProperties securityProperties;
    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Autowired
    public SpringSecurityConfig(SecurityWhiteListProperties securityProperties, JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.securityProperties = securityProperties;
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange
                .pathMatchers(securityProperties.getWhitelist().toArray(new String[0])).permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
}