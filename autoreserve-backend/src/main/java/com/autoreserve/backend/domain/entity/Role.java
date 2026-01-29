package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;

/**
 * Entidad que representa los perfiles de acceso o roles dentro del sistema.
 * Mapea la tabla "role" y es fundamental para la seguridad basada en autoridades (RBAC),
 * permitiendo diferenciar entre clientes, administradores y personal de soporte.
 */
@Entity
@Table(name = "role")
public class Role {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol (ej. ROLE_USER, ROLE_ADMIN).
     * Se almacena de forma única para evitar duplicidad de perfiles de seguridad.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Role() {
    }

    // Métodos de acceso (Getters y Setters)

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