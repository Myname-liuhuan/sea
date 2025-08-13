package com.example.sea.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.sql.DataSource;

@Configuration
public class JdbcTokenStoreConfig {

    @Bean
    public RegisteredClientRepository jdbcRegisteredClientRepository(DataSource dataSource) {
        return new JdbcRegisteredClientRepository(new JdbcTemplate(dataSource));
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(DataSource dataSource, 
                                                         RegisteredClientRepository jdbcRegisteredClientRepository) {
        return new JdbcOAuth2AuthorizationService(new JdbcTemplate(dataSource), jdbcRegisteredClientRepository);
    }
}
