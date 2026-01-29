package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que extiende la información básica del usuario.
 * Mapea la tabla "user_profile" y almacena datos complementarios y legales
 * necesarios para la validación de contratos de alquiler de vehículos.
 */
@Entity
@Table(name = "user_profile")
public class UserProfile {

    /**
     * Identificador único del perfil.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación uno a uno con la entidad User.
     * Vincula este perfil a una cuenta de usuario específica de forma obligatoria.
     */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
     * Dirección residencial del usuario.
     */
    @Column(length = 255)
    private String address;

    /**
     * Ciudad de residencia del usuario.
     */
    @Column(length = 100)
    private String city;

    /**
     * Fecha de nacimiento del usuario.
     * Fundamental para verificar requisitos de edad mínima para conducir.
     */
    private LocalDate birthDate;

    /**
     * Número de licencia de conducir.
     * Documento esencial para habilitar la entrega de un vehículo.
     */
    @Column(length = 100)
    private String drivingLicense;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public UserProfile() {
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }
}