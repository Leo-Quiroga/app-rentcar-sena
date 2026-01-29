package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa a los usuarios del sistema.
 * Almacena la información de perfil, credenciales de acceso y la relación
 * con el sistema de permisos a través de roles.
 */
@Entity
@Table(name = "user")
public class User {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre(s) del usuario.
     */
    @Column(nullable = false, length = 100)
    private String firstName;

    /**
     * Apellido(s) del usuario.
     */
    @Column(length = 100)
    private String lastName;

    /**
     * Correo electrónico único, utilizado como principal identificador de acceso (username).
     */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Contraseña almacenada en formato Hash (BCrypt) para garantizar la seguridad.
     */
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /**
     * Número telefónico de contacto del usuario.
     */
    @Column(length = 30)
    private String phone;

    /**
     * Fecha y hora de registro del usuario en la plataforma.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Estado operativo del usuario dentro del sistema.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Rol asignado que define las autoridades y permisos del usuario.
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * Constructor por defecto. Inicializa automáticamente la fecha de creación
     * y establece el estado inicial como activo.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Método de conveniencia para verificar si el usuario tiene permiso de acceso.
     * @return true si el estado es ACTIVE.
     */
    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }

    /**
     * Enum que define los posibles estados de cuenta de un usuario.
     */
    public enum Status {
        /** El usuario puede operar normalmente. */
        ACTIVE,
        /** El usuario ha sido desactivado (ej. baja voluntaria). */
        INACTIVE,
        /** El acceso ha sido restringido por administración. */
        SUSPENDED
    }
}