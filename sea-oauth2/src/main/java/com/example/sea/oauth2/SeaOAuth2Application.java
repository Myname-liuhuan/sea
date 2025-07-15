package com.example.sea.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@SpringBootApplication
public class SeaOAuth2Application {
    
    public static void main(String[] args) {
        SpringApplication.run(SeaOAuth2Application.class, args);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    // 新增一个根路径映射，避免登录后访问 / 返回404
    @org.springframework.web.bind.annotation.RestController
    static class RootController {
        @org.springframework.web.bind.annotation.GetMapping("/")
        public String index() {
            return "Sea OAuth2 Server is running.";
        }
    }
}
