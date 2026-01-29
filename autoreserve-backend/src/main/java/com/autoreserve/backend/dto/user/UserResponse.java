package com.autoreserve.backend.dto.user;

import java.time.LocalDateTime;

/**
 * DTO para la visualización de datos públicos o administrativos de un usuario.
 * Proporciona una vista simplificada y segura de la entidad User.
 */
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String phone;
    private LocalDateTime createdAt;
    /**
     * Constructor para proyecciones directas desde la capa de servicio o repositorios.
     */
    public UserResponse(Long id, String firstName, String lastName, String email, String phone, String role, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this .createdAt = createdAt;
    }

    /* ================= GETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}