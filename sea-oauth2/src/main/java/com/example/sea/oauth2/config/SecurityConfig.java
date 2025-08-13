package com.example.sea.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean 
	@Order(2)
	SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
		http.anonymous(AnonymousConfigurer::disable);
		http.cors(CorsConfigurer::disable).authorizeHttpRequests(authorize ->
			authorize
			.requestMatchers("/client/**", "/swagger-ui/**", "/v3/api-docs/**", "/vendor/**", "/favicon.ico", "/actuator/**").permitAll()
			.requestMatchers("/user/**").hasAuthority("ADMIN")
			.requestMatchers("/user/**").hasRole("ADMIN")
			);
		http
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().authenticated())
			.formLogin(Customizer.withDefaults());
		http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false));
		http.csrf(CsrfConfigurer::disable); // 确保CSRF在最后被禁用
	
		return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
