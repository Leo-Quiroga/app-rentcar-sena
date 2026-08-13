package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMessageControllerTest {

    @Mock
    private ContactMessageRepository messageRepository;

    @Mock
    private MessageReplyRepository replyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails principal;

    private ContactMessageController controller;
    private User client;
    private User admin;
    private Role clientRole;
    private Role adminRole;
    private ContactMessage ticket;
    private MessageReply reply;

    @BeforeEach
    void setUp() {
        controller = new ContactMessageController(messageRepository, replyRepository, userRepository);

        clientRole = new Role();
        clientRole.setName("CLIENT");

        adminRole = new Role();
        adminRole.setName("ADMIN");

        client = new User();
        client.setId(1L);
        client.setEmail("cliente@example.com");
        client.setFirstName("Cliente");
        client.setLastName("Ejemplo");
        client.setRole(clientRole);

        admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("Soporte");
        admin.setRole(adminRole);

        ticket = new ContactMessage();
        ticket.setId(1L);
        ticket.setUser(client);
        ticket.setSenderName("Cliente Ejemplo");
        ticket.setSenderEmail("cliente@example.com");
        ticket.setStatus(MessageStatus.OPEN);
        ticket.setSubject("Consulta");
        ticket.setType(MessageType.PREGUNTA);
        ticket.setCreatedAt(LocalDateTime.now());

        reply = new MessageReply();
        reply.setId(1L);
        reply.setContent("Respuesta");
        reply.setSentBy(MessageSender.ADMIN);
        reply.setAuthorName("Soporte AutoReserve");
        reply.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createTicket_AsAnonymousUser_CreatesTicketWithBodyData() {
        when(messageRepository.save(any())).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });
        when(replyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Map<String, String> body = Map.of(
                "name", "Anonimo",
                "email", "anon@example.com",
                "subject", "Consulta",
                "type", "PREGUNTA",
                "message", "Hola"
        );

        ResponseEntity<?> response = controller.createTicket(body, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
        verify(messageRepository).save(any(ContactMessage.class));
        verify(replyRepository).save(any(MessageReply.class));
    }

    @Test
    void createTicket_AsAuthenticatedUser_UsesUserDetails() {
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.save(any())).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });
        when(replyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Map<String, String> body = Map.of(
                "subject", "Consulta",
                "type", "PREGUNTA",
                "message", "Hola"
        );

        ResponseEntity<?> response = controller.createTicket(body, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
        verify(messageRepository).save(any(ContactMessage.class));
        verify(replyRepository).save(any(MessageReply.class));
    }

    @Test
    void getUnreadCount_AsAdmin_ReturnsOpenCount() {
        when(principal.getUsername()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        ticket.setStatus(MessageStatus.CLOSED);
        ContactMessage openTicket = new ContactMessage();
        openTicket.setStatus(MessageStatus.OPEN);
        when(messageRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(openTicket, ticket));

        ResponseEntity<?> response = controller.getUnreadCount(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("unreadCount")).isEqualTo(1L);
    }

    @Test
    void getUnreadCount_AsClient_ReturnsAnsweredCount() {
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        ContactMessage answered = new ContactMessage();
        answered.setStatus(MessageStatus.ANSWERED);
        answered.setUser(client);
        when(messageRepository.findByUserOrderByCreatedAtDesc(client)).thenReturn(List.of(answered, ticket));

        ResponseEntity<?> response = controller.getUnreadCount(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("unreadCount")).isEqualTo(1L);
    }

    @Test
    void getMyTicketDetail_ReturnsForbiddenWhenTicketDoesNotBelongToUser() {
        User other = new User();
        other.setId(2L);
        ticket.setUser(other);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.getMyTicketDetail(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Sin permisos");
    }

    @Test
    void getMyTicketDetail_ConvertsAnsweredTicketToInProgress() {
        ticket.setStatus(MessageStatus.ANSWERED);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(replyRepository.findByContactMessageIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(reply));

        ResponseEntity<?> response = controller.getMyTicketDetail(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.IN_PROGRESS);
        verify(messageRepository).save(ticket);
    }

    @Test
    void clientReply_ReturnsBadRequestWhenTicketClosed() {
        ticket.setStatus(MessageStatus.CLOSED);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.clientReply(1L, Map.of("message", "Sigo con dudas"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("No se puede responder a un ticket cerrado");
    }

    @Test
    void clientReply_ReturnsSuccessWhenTicketOpen() {
        ticket.setStatus(MessageStatus.OPEN);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.clientReply(1L, Map.of("message", "Sigo con dudas"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.OPEN);
        verify(replyRepository).save(any(MessageReply.class));
        verify(messageRepository).save(ticket);
    }

    @Test
    void closeTicket_ReturnsSuccess() {
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.closeTicket(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.CLOSED);
        verify(messageRepository).save(ticket);
    }

    @Test
    void adminStartConversation_ReturnsInternalErrorWhenClientMissing() {
        when(principal.getUsername()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.adminStartConversation(Map.of("userId", "0", "subject", "Ayuda", "message", "Hola"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void adminStartConversation_ReturnsSuccess() {
        when(principal.getUsername()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ResponseEntity<?> response = controller.adminStartConversation(Map.of("userId", client.getId().toString(), "subject", "Ayuda", "message", "Hola"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("ticketId")).isEqualTo(2L);
        verify(replyRepository).save(any(MessageReply.class));
    }

    @Test
    void getTicketDetail_OpenTicketMovesToInProgress() {
        ticket.setStatus(MessageStatus.OPEN);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(replyRepository.findByContactMessageIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(reply));

        ResponseEntity<?> response = controller.getTicketDetail(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.IN_PROGRESS);
        verify(messageRepository).save(ticket);
    }

    @Test
    void adminReply_ReturnsBadRequestWhenTicketClosed() {
        ticket.setStatus(MessageStatus.CLOSED);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.adminReply(1L, Map.of("message", "Hola"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("No se puede responder a un ticket cerrado");
    }

    @Test
    void adminReply_ReturnsSuccess() {
        ticket.setStatus(MessageStatus.IN_PROGRESS);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.adminReply(1L, Map.of("message", "Hola"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.ANSWERED);
        verify(replyRepository).save(any(MessageReply.class));
        verify(messageRepository).save(ticket);
    }

    @Test
    void adminCloseTicket_ReturnsSuccess() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));

        ResponseEntity<?> response = controller.adminCloseTicket(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.CLOSED);
        verify(messageRepository).save(ticket);
    }
}
