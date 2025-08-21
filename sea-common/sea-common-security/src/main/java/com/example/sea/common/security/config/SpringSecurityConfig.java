package com.example.sea.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.sea.common.security.filter.JwtAuthenticationWebFilter;
import com.example.sea.common.security.properties.SecurityWhiteListProperties;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final SecurityWhiteListProperties securityProperties;
    private final JwtAuthenticationWebFilter jwtAuthenticationFilter;

    @Autowired
    public SpringSecurityConfig(SecurityWhiteListProperties securityProperties, JwtAuthenticationWebFilter jwtAuthenticationFilter) {
        this.securityProperties = securityProperties;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getWhitelist().toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
