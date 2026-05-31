package com.example.sea.common.feign.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "feign.internal.token")
public class FeignTokenProperties {

    private User user = new User();
    private List<String> roles = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (user == null) {
            errors.add("feign.internal.token.user");
        } else {
            if (user.getId() == null) {
                errors.add("feign.internal.token.user.id");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                errors.add("feign.internal.token.user.name");
            }
        }
        if (roles == null || roles.isEmpty()) {
            errors.add("feign.internal.token.roles");
        }
        if (authorities == null || authorities.isEmpty()) {
            errors.add("feign.internal.token.authorities");
        }
        return errors;
    }

    public static class User {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
