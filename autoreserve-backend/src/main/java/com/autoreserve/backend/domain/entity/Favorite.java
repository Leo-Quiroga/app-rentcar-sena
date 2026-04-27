package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que gestiona la relación de modelos de vehículos favoritos por parte de los usuarios.
 * Mapea la tabla "favorite" y garantiza, mediante restricciones de unicidad,
 * que un usuario no duplique un mismo modelo en su lista de favoritos.
 */
@Entity
@Table(
        name = "favorite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "car_model_id"})
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
     * Referencia al usuario que marca el modelo como favorito.
     * Campo obligatorio vinculado a la entidad User.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Referencia al modelo de vehículo marcado como favorito.
     * Campo obligatorio vinculado a la entidad CarModel.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model_id")
    private CarModel carModel;

    /**
     * Fecha y hora exacta en la que se creó el registro de favorito.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor por defecto para la persistencia de JPA.
     */
    public Favorite() {
        this.createdAt = LocalDateTime.now();
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

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}