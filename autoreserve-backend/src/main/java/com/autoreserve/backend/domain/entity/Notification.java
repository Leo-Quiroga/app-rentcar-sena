package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que gestiona las notificaciones dirigidas a los usuarios del sistema.
 * Mapea la tabla "notification" y permite el seguimiento de alertas informativas,
 * confirmaciones de reserva o avisos de mantenimiento.
 */
@Entity
@Table(name = "notification")
public class Notification {

    /**
     * Identificador único de la notificación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia al usuario destinatario de la notificación.
     * Relación obligatoria vinculada a la entidad User.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Contenido textual de la notificación.
     * Definido como TEXT para permitir mensajes detallados.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Categorización de la notificación (ej. INFO, WARNING, SUCCESS).
     * Se almacena como una cadena de texto basada en la enumeración NotificationType.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    /**
     * Fecha y hora en la que se generó o envió la notificación.
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * Indicador de estado de lectura.
     * true si el usuario ha visto la notificación, false en caso contrario.
     */
    @Column(name = "read_flag", nullable = false)
    private boolean read;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Notification() {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}