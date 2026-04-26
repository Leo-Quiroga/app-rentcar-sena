package com.autoreserve.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa un mensaje dentro del hilo de un ticket de soporte.
 * Puede ser enviado por el cliente o por el admin.
 */
@Entity
@Table(name = "message_reply")
public class MessageReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contact_message_id", nullable = false)
    private ContactMessage contactMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageSender sentBy;

    @Column(nullable = false, length = 100)
    private String authorName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MessageReply() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ContactMessage getContactMessage() { return contactMessage; }
    public void setContactMessage(ContactMessage contactMessage) { this.contactMessage = contactMessage; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageSender getSentBy() { return sentBy; }
    public void setSentBy(MessageSender sentBy) { this.sentBy = sentBy; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
