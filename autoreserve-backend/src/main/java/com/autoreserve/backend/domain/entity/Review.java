package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que gestiona las valoraciones y comentarios de los clientes sobre los vehículos.
 * Permite recopilar el feedback de los usuarios para generar una reputación basada en
 * puntuaciones numéricas y testimonios escritos.
 */
@Entity
@Table(name = "review")
public class Review {

    /**
     * Identificador único de la reseña.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia al usuario que realiza la valoración.
     * Relación obligatoria vinculada a la entidad User.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Referencia al vehículo que está siendo evaluado.
     * Relación obligatoria vinculada a la entidad Car.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id")
    private Car car;

    /**
     * Calificación numérica otorgada por el usuario.
     * Comúnmente manejada en un rango de 1 (mínimo) a 5 (máximo).
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Comentario textual o testimonio sobre la experiencia con el vehículo.
     * Definido como TEXT para no limitar la extensión de la opinión.
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * Fecha y hora en la que se publicó la reseña.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor por defecto requerido por el motor de persistencia.
     */
    public Review() {
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}