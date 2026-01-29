package com.autoreserve.backend.dto.auth;

/**
 * Respuesta enviada tras una autenticación exitosa.
 * Contiene los datos de identidad y el token de acceso para el cliente.
 */
public class LoginResponse {

    private Long userId;
    private String email;
    private String role;
    private String token;

    public LoginResponse() {
    }

    /**
     * Constructor para inicializar la respuesta completa de autenticación.
     */
    public LoginResponse(Long userId, String email, String role, String token) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    /* ================= GETTERS & SETTERS ================= */

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}