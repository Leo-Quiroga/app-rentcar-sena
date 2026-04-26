package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa un ticket de soporte creado por un usuario.
 * Puede tener múltiples respuestas en hilo (MessageReply).
 */
@Entity
@Table(name = "contact_message")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario autenticado que creó el ticket (nullable si es anónimo) */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Nombre del remitente (para usuarios anónimos) */
    @Column(nullable = false, length = 100)
    private String senderName;

    /** Email del remitente */
    @Column(nullable = false, length = 200)
    private String senderEmail;

    /** Asunto del ticket */
    @Column(nullable = false, length = 200)
    private String subject;

    /** Tipo de mensaje */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    /** Estado actual del ticket */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    /** Hilo de mensajes del ticket */
    @OneToMany(mappedBy = "contactMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<MessageReply> replies;

    public ContactMessage() {
        this.status = MessageStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<MessageReply> getReplies() { return replies; }
    public void setReplies(List<MessageReply> replies) { this.replies = replies; }
}
