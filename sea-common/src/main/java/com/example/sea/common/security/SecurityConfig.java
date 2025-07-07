// package com.example.sea.common.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.web.server.SecurityWebFilterChain;

// /**
//  * 各业务模块认证配置类
//  * @author liuhuan
//  * @date 2025-06-25
//  */
// @Configuration
// @EnableWebFluxSecurity
// public class SecurityConfig {

//   @Bean
//   SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//     http
//       .authorizeExchange(exchanges ->
//         exchanges.anyExchange().authenticated()
//       )
//       .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> 
//         jwt.jwkSetUri("https://auth-server/.well-known/jwks.json")
//       ));
//     return http.build();
//   }
// }

