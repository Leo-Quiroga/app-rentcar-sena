package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMessageRepository messageRepository;
    private final MessageReplyRepository replyRepository;
    private final UserRepository userRepository;

    public ContactMessageController(ContactMessageRepository messageRepository,
                                    MessageReplyRepository replyRepository,
                                    UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
    }

    // ── ENDPOINTS PÚBLICOS / CLIENTE ─────────────────────────────────────────

    /** Crear nuevo ticket (público o autenticado) */
    @PostMapping
    public ResponseEntity<?> createTicket(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            ContactMessage ticket = new ContactMessage();

            if (principal != null) {
                userRepository.findByEmail(principal.getUsername()).ifPresent(u -> {
                    ticket.setUser(u);
                    ticket.setSenderName(u.getFirstName() + " " + u.getLastName());
                    ticket.setSenderEmail(u.getEmail());
                });
            } else {
                ticket.setSenderName(body.getOrDefault("name", "Anónimo"));
                ticket.setSenderEmail(body.getOrDefault("email", ""));
            }

            ticket.setSubject(body.getOrDefault("subject", "Sin asunto"));
            ticket.setType(MessageType.valueOf(body.getOrDefault("type", "PREGUNTA")));

            ContactMessage saved = messageRepository.save(ticket);

            MessageReply firstReply = new MessageReply();
            firstReply.setContactMessage(saved);
            firstReply.setContent(body.getOrDefault("message", ""));
            firstReply.setSentBy(MessageSender.CLIENT);
            firstReply.setAuthorName(saved.getSenderName());
            replyRepository.save(firstReply);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tu mensaje fue enviado exitosamente. Te responderemos pronto.",
                "ticketId", saved.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Conteo de mensajes no leídos según el rol del usuario autenticado */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            long count;
            if ("ADMIN".equals(user.getRole().getName())) {
                // Admin: tickets en OPEN (clientes esperando respuesta)
                count = messageRepository.findAllByOrderByCreatedAtDesc().stream()
                        .filter(t -> t.getStatus() == MessageStatus.OPEN)
                        .count();
            } else {
                // Cliente: sus tickets en ANSWERED (admin respondió, cliente no ha visto)
                count = messageRepository.findByUserOrderByCreatedAtDesc(user).stream()
                        .filter(t -> t.getStatus() == MessageStatus.ANSWERED)
                        .count();
            }

            return ResponseEntity.ok(Map.of("unreadCount", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("unreadCount", 0));
        }
    }

    /** Mis tickets (cliente autenticado) */
    @GetMapping("/my")
    public ResponseEntity<?> getMyTickets(@AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<ContactMessage> tickets = messageRepository.findByUserOrderByCreatedAtDesc(user);
            return ResponseEntity.ok(tickets.stream().map(this::toSummary).toList());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Ver hilo completo de un ticket (cliente autenticado) */
    @GetMapping("/my/{id}")
    public ResponseEntity<?> getMyTicketDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

            if (ticket.getUser() == null || !ticket.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }

            // Al ver el hilo, si estaba ANSWERED pasa a IN_PROGRESS (cliente lo leyó)
            if (ticket.getStatus() == MessageStatus.ANSWERED) {
                ticket.setStatus(MessageStatus.IN_PROGRESS);
                ticket.setUpdatedAt(LocalDateTime.now());
                messageRepository.save(ticket);
            }

            List<MessageReply> replies = replyRepository
                    .findByContactMessageIdOrderByCreatedAtAsc(id);

            return ResponseEntity.ok(Map.of(
                "ticket", toSummary(ticket),
                "replies", replies.stream().map(this::toReplyMap).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Cliente responde en el hilo (contra-pregunta) */
    @PostMapping("/my/{id}/reply")
    public ResponseEntity<?> clientReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

            if (ticket.getUser() == null || !ticket.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }
            if (ticket.getStatus() == MessageStatus.CLOSED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No se puede responder a un ticket cerrado"));
            }

            MessageReply reply = new MessageReply();
            reply.setContactMessage(ticket);
            reply.setContent(body.getOrDefault("message", ""));
            reply.setSentBy(MessageSender.CLIENT);
            reply.setAuthorName(user.getFirstName() + " " + user.getLastName());
            replyRepository.save(reply);

            ticket.setStatus(MessageStatus.OPEN);
            ticket.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(ticket);

            return ResponseEntity.ok(Map.of("success", true, "message", "Respuesta enviada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Cliente cierra el ticket como resuelto */
    @PutMapping("/my/{id}/close")
    public ResponseEntity<?> closeTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

            if (ticket.getUser() == null || !ticket.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }

            ticket.setStatus(MessageStatus.CLOSED);
            ticket.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(ticket);

            return ResponseEntity.ok(Map.of("success", true, "message", "Caso cerrado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ── ENDPOINTS ADMIN ──────────────────────────────────────────────────────

    /** Listar todos los tickets */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTickets() {
        List<ContactMessage> tickets = messageRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(tickets.stream().map(this::toSummary).toList());
    }

    /** Admin inicia una conversación con un cliente */
    @PostMapping("/admin/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminStartConversation(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User admin = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

            Long clientId = Long.parseLong(body.getOrDefault("userId", "0"));
            User client = userRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            ContactMessage ticket = new ContactMessage();
            ticket.setUser(client);
            ticket.setSenderName(client.getFirstName() + " " + client.getLastName());
            ticket.setSenderEmail(client.getEmail());
            ticket.setSubject(body.getOrDefault("subject", "Mensaje de AutoReserve"));
            ticket.setType(MessageType.valueOf(body.getOrDefault("type", "SOLICITUD")));
            ticket.setStatus(MessageStatus.ANSWERED); // Admin ya respondió (inició él)

            ContactMessage saved = messageRepository.save(ticket);

            MessageReply firstReply = new MessageReply();
            firstReply.setContactMessage(saved);
            firstReply.setContent(body.getOrDefault("message", ""));
            firstReply.setSentBy(MessageSender.ADMIN);
            firstReply.setAuthorName("Soporte AutoReserve");
            replyRepository.save(firstReply);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mensaje enviado al cliente exitosamente",
                "ticketId", saved.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Ver hilo completo de un ticket (admin) */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTicketDetail(@PathVariable Long id) {
        try {
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

            if (ticket.getStatus() == MessageStatus.OPEN) {
                ticket.setStatus(MessageStatus.IN_PROGRESS);
                ticket.setUpdatedAt(LocalDateTime.now());
                messageRepository.save(ticket);
            }

            List<MessageReply> replies = replyRepository
                    .findByContactMessageIdOrderByCreatedAtAsc(id);

            return ResponseEntity.ok(Map.of(
                "ticket", toSummary(ticket),
                "replies", replies.stream().map(this::toReplyMap).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Admin responde en el hilo */
    @PostMapping("/admin/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

            if (ticket.getStatus() == MessageStatus.CLOSED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No se puede responder a un ticket cerrado"));
            }

            MessageReply reply = new MessageReply();
            reply.setContactMessage(ticket);
            reply.setContent(body.getOrDefault("message", ""));
            reply.setSentBy(MessageSender.ADMIN);
            reply.setAuthorName("Soporte AutoReserve");
            replyRepository.save(reply);

            ticket.setStatus(MessageStatus.ANSWERED);
            ticket.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(ticket);

            return ResponseEntity.ok(Map.of("success", true, "message", "Respuesta enviada al cliente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Admin cierra el ticket */
    @PutMapping("/admin/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCloseTicket(@PathVariable Long id) {
        try {
            ContactMessage ticket = messageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
            ticket.setStatus(MessageStatus.CLOSED);
            ticket.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(ticket);
            return ResponseEntity.ok(Map.of("success", true, "message", "Ticket cerrado"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private Map<String, Object> toSummary(ContactMessage t) {
        return Map.of(
            "id", t.getId(),
            "senderName", t.getSenderName(),
            "senderEmail", t.getSenderEmail(),
            "subject", t.getSubject(),
            "type", t.getType().name(),
            "status", t.getStatus().name(),
            "createdAt", t.getCreatedAt().toString(),
            "updatedAt", t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : "",
            "userId", t.getUser() != null ? t.getUser().getId() : null
        );
    }

    private Map<String, Object> toReplyMap(MessageReply r) {
        return Map.of(
            "id", r.getId(),
            "content", r.getContent(),
            "sentBy", r.getSentBy().name(),
            "authorName", r.getAuthorName(),
            "createdAt", r.getCreatedAt().toString()
        );
    }
}
