package com.cardio.cardiotrack.dto;

public class AuthResponseDTO {

    private Long userId;
    private String email;
    private String role;
    private String message;
    private String token;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(Long userId, String email, String role, String message, String token) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.message = message;
        this.token=token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}