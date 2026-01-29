package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que gestiona la relación de vehículos favoritos por parte de los usuarios.
 * Mapea la tabla "favorite" y garantiza, mediante restricciones de unicidad,
 * que un usuario no duplique un mismo vehículo en su lista de favoritos.
 */
@Entity
@Table(
        name = "favorite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "car_id"})
        }
)
public class Favorite {

    /**
     * Identificador único del registro de favorito.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia al usuario que marca el vehículo como favorito.
     * Campo obligatorio vinculado a la entidad User.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Referencia al vehículo marcado como favorito.
     * Campo obligatorio vinculado a la entidad Car.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id")
    private Car car;

    /**
     * Fecha y hora exacta en la que se creó el registro de favorito.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor por defecto para la persistencia de JPA.
     */
    public Favorite() {
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}