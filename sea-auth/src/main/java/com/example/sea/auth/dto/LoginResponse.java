package com.example.sea.auth.dto;

/**
 * 登录响应DTO
 * @author liuhuan
 * @date 2025-08-04
 */
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long expiresIn;

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String refreshToken, Long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
