package com.autoreserve.backend.dto.user;

/**
 * DTO para la visualización de datos públicos o administrativos de un usuario.
 * Proporciona una vista simplificada y segura de la entidad User.
 */
public class UserResponse {

    private Long id;
    private String email;
    private String role;

    /**
     * Constructor para proyecciones directas desde la capa de servicio o repositorios.
     */
    public UserResponse(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    /* ================= GETTERS ================= */

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}