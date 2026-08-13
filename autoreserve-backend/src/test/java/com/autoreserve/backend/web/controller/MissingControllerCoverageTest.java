package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.reservation.ReservationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissingControllerCoverageTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ContactMessageRepository messageRepository;
    @Mock
    private MessageReplyRepository replyRepository;
    @Mock
    private FaqRepository faqRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarModelRepository carModelRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserDetails principal;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static User createUser(Long id, String email, String firstName, String lastName, String roleName) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Role role = new Role();
        role.setName(roleName);
        user.setRole(role);
        return user;
    }

    private static ContactMessage createTicket(Long id, User user, MessageStatus status) {
        ContactMessage ticket = new ContactMessage();
        ticket.setId(id);
        ticket.setUser(user);
        ticket.setStatus(status);
        ticket.setSenderName(user != null ? user.getFirstName() + " " + user.getLastName() : "Anónimo");
        ticket.setSenderEmail(user != null ? user.getEmail() : "anon@example.com");
        ticket.setSubject("Prueba");
        ticket.setType(MessageType.PREGUNTA);
        ticket.setCreatedAt(LocalDateTime.now().minusDays(1));
        ticket.setUpdatedAt(LocalDateTime.now().minusHours(1));
        return ticket;
    }

    private static MessageReply createReply(Long id, ContactMessage ticket) {
        MessageReply reply = new MessageReply();
        reply.setId(id);
        reply.setContactMessage(ticket);
        reply.setContent("Respuesta de prueba");
        reply.setSentBy(MessageSender.ADMIN);
        reply.setAuthorName("Admin");
        reply.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        return reply;
    }

    @Test
    void clientControllerClientDashboardUsesSecurityContext() {
        ClientController controller = new ClientController();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("cliente@example.com");
        @SuppressWarnings("rawtypes")
        java.util.Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        SecurityContextHolder.setContext(securityContext);

        String response = controller.clientDashboard();

        assertThat(response).contains("Welcome CLIENT");
        verify(authentication, times(1)).getName();
    }

    @Test
    void contactMessageControllerCreatesAnonymousTicketAndReply() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        Map<String, String> body = Map.of(
                "subject", "Soporte",
                "type", "PREGUNTA",
                "message", "Necesito ayuda"
        );

        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(replyRepository.save(any(MessageReply.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.createTicket(body, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertThat(bodyMap).containsEntry("success", true);
        verify(messageRepository, times(1)).save(any(ContactMessage.class));
        verify(replyRepository, times(1)).save(any(MessageReply.class));
    }

    @Test
    void contactMessageControllerAdminUnreadCountCountsOpenTickets() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User admin = createUser(1L, "admin@example.com", "Admin", "Prueba", "ADMIN");
        when(principal.getUsername()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        List<ContactMessage> tickets = List.of(
                createTicket(1L, null, MessageStatus.OPEN),
                createTicket(2L, null, MessageStatus.CLOSED)
        );
        when(messageRepository.findAllByOrderByCreatedAtDesc()).thenReturn(tickets);

        ResponseEntity<?> response = controller.getUnreadCount(principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) response.getBody()).get("unreadCount")).isEqualTo(1L);
    }

    @Test
    void contactMessageControllerMyTicketDetailUpdatesAnsweredTicket() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User client = createUser(2L, "client@example.com", "Cliente", "Prueba", "CLIENT");
        ContactMessage ticket = createTicket(1L, client, MessageStatus.ANSWERED);
        List<MessageReply> replies = List.of(createReply(1L, ticket));

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(replyRepository.findByContactMessageIdOrderByCreatedAtAsc(1L)).thenReturn(replies);
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.getMyTicketDetail(1L, principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertThat(bodyMap).containsKey("ticket");
        verify(messageRepository, times(1)).save(any(ContactMessage.class));
    }

    @Test
    void contactMessageControllerAdminStartConversationAndCloseTicket() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User admin = createUser(10L, "admin@example.com", "Admin", "Prueba", "ADMIN");
        User client = createUser(11L, "cliente@example.com", "Cliente", "Prueba", "CLIENT");
        when(principal.getUsername()).thenReturn(admin.getEmail());
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(11L)).thenReturn(Optional.of(client));
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(replyRepository.save(any(MessageReply.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> body = Map.of(
                "userId", "11",
                "subject", "Bienvenida",
                "type", "SOLICITUD",
                "message", "Hola cliente"
        );
        ResponseEntity<?> startResponse = controller.adminStartConversation(body, principal);
        assertThat(startResponse.getStatusCodeValue()).isEqualTo(200);
        verify(messageRepository, times(1)).save(any(ContactMessage.class));
        verify(replyRepository, times(1)).save(any(MessageReply.class));

        when(messageRepository.findById(1L)).thenReturn(Optional.of(createTicket(1L, client, MessageStatus.OPEN)));
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(replyRepository.findByContactMessageIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(createReply(1L, createTicket(1L, client, MessageStatus.OPEN))));
        ResponseEntity<?> detailResponse = controller.getTicketDetail(1L);
        assertThat(detailResponse.getStatusCodeValue()).isEqualTo(200);
        verify(messageRepository, times(2)).save(any(ContactMessage.class));

        ResponseEntity<?> adminReplyResponse = controller.adminReply(1L, Map.of("message", "Otra respuesta"), principal);
        assertThat(adminReplyResponse.getStatusCodeValue()).isEqualTo(200);
        verify(replyRepository, times(2)).save(any(MessageReply.class));

        when(messageRepository.findById(1L)).thenReturn(Optional.of(createTicket(1L, client, MessageStatus.OPEN)));
        ResponseEntity<?> adminCloseResponse = controller.adminCloseTicket(1L);
        assertThat(adminCloseResponse.getStatusCodeValue()).isEqualTo(200);
    }
 
    @Test
    void contactMessageControllerCreatesAuthenticatedTicketAndReply() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User client = createUser(3L, "client-auth@example.com", "Cliente", "Auth", "CLIENT");
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> {
            ContactMessage saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        when(replyRepository.save(any(MessageReply.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        Map<String, String> body = Map.of(
                "subject", "Consulta autenticada",
                "type", "PREGUNTA",
                "message", "Necesito soporte"
        );
 
        ResponseEntity<?> response = controller.createTicket(body, principal);
 
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertThat(bodyMap).containsEntry("success", true);
        assertThat(bodyMap.get("ticketId")).isEqualTo(2L);
        verify(messageRepository, times(1)).save(any(ContactMessage.class));
        verify(replyRepository, times(1)).save(any(MessageReply.class));
    }
 
    @Test
    void contactMessageControllerReturnsMyTicketsAndForbiddenDetail() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User client = createUser(4L, "client-list@example.com", "Cliente", "List", "CLIENT");
        ContactMessage ticket = createTicket(10L, client, MessageStatus.OPEN);
 
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findByUserOrderByCreatedAtDesc(client)).thenReturn(List.of(ticket));
 
        ResponseEntity<?> listResponse = controller.getMyTickets(principal);
        assertThat(listResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(((List<?>) listResponse.getBody())).hasSize(1);
 
        User otherClient = createUser(5L, "other@example.com", "Otro", "User", "CLIENT");
        when(principal.getUsername()).thenReturn(otherClient.getEmail());
        when(userRepository.findByEmail(otherClient.getEmail())).thenReturn(Optional.of(otherClient));
        when(messageRepository.findById(10L)).thenReturn(Optional.of(ticket));
 
        ResponseEntity<?> forbiddenResponse = controller.getMyTicketDetail(10L, principal);
        assertThat(forbiddenResponse.getStatusCodeValue()).isEqualTo(403);
    }
 
    @Test
    void contactMessageControllerClientReplyAndCloseTicketFlow() {
        ContactMessageController controller = new ContactMessageController(messageRepository, replyRepository, userRepository);
        User client = createUser(6L, "client-reply@example.com", "Cliente", "Reply", "CLIENT");
        ContactMessage ticket = createTicket(20L, client, MessageStatus.OPEN);
 
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(messageRepository.findById(20L)).thenReturn(Optional.of(ticket));
        when(replyRepository.save(any(MessageReply.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(messageRepository.save(any(ContactMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        ResponseEntity<?> replyResponse = controller.clientReply(20L, Map.of("message", "Gracias por la ayuda"), principal);
        assertThat(replyResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) replyResponse.getBody()).get("success")).isEqualTo(true);
        verify(replyRepository, times(1)).save(any(MessageReply.class));
 
        ResponseEntity<?> closeResponse = controller.closeTicket(20L, principal);
        assertThat(closeResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(ticket.getStatus()).isEqualTo(MessageStatus.CLOSED);
    }
 
    @Test
    void faqControllerCRUDPaths() {
        FaqController controller = new FaqController(faqRepository);
        Faq faq = new Faq();
        faq.setId(1L);
        faq.setQuestion("¿Qué es AutoReserve?");
        faq.setAnswer("Una plataforma de reservas de automóviles.");
        faq.setSortOrder(1);

        when(faqRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(faq));
        ResponseEntity<List<Faq>> allResponse = controller.getAllFaqs();
        assertThat(allResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(allResponse.getBody()).containsExactly(faq);

        when(faqRepository.findMaxSortOrder()).thenReturn(2);
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> {
            Faq saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        ResponseEntity<?> createResponse = controller.createFaq(Map.of("question", "¿Test?", "answer", "Sí"));
        assertThat(createResponse.getStatusCodeValue()).isEqualTo(200);

        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));
        ResponseEntity<?> updateResponse = controller.updateFaq(1L, Map.of("answer", "Respuesta actualizada"));
        assertThat(updateResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(faq.getAnswer()).isEqualTo("Respuesta actualizada");

        Faq otherFaq = new Faq();
        otherFaq.setId(2L);
        otherFaq.setSortOrder(1);
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));
        when(faqRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(otherFaq));
        ResponseEntity<?> deleteResponse = controller.deleteFaq(1L);
        assertThat(deleteResponse.getStatusCodeValue()).isEqualTo(200);
        verify(faqRepository).saveAll(List.of(otherFaq));
    }

    @Test
    void policyControllerCRUDPaths() {
        PolicyController controller = new PolicyController(policyRepository);
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setTitle("Política 1");
        policy.setContent("Contenido");
        policy.setSortOrder(1);

        when(policyRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(policy));
        ResponseEntity<List<Policy>> allResponse = controller.getAllPolicies();
        assertThat(allResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(allResponse.getBody()).containsExactly(policy);

        when(policyRepository.findMaxSortOrder()).thenReturn(1);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> {
            Policy saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        ResponseEntity<?> createResponse = controller.createPolicy(Map.of("title", "Nueva política", "content", "Contenido"));
        assertThat(createResponse.getStatusCodeValue()).isEqualTo(200);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        ResponseEntity<?> updateResponse = controller.updatePolicy(1L, Map.of("title", "Actualizada"));
        assertThat(updateResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(policy.getTitle()).isEqualTo("Actualizada");

        Policy policy2 = new Policy();
        policy2.setId(2L);
        policy2.setSortOrder(1);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(policy2));
        ResponseEntity<?> deleteResponse = controller.deletePolicy(1L);
        assertThat(deleteResponse.getStatusCodeValue()).isEqualTo(200);
        verify(policyRepository).saveAll(List.of(policy2));
    }

    @Test
    void favoriteControllerHandlesFavoritesAndIds() {
        FavoriteController controller = new FavoriteController(favoriteRepository, carRepository, carModelRepository, userRepository);
        User client = createUser(20L, "cliente2@example.com", "Cliente", "Dos", "CLIENT");
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        CarModel model = new CarModel();
        model.setId(10L);
        model.setBrand("Toyota");
        model.setModel("Corolla");
        model.setYear(2024);
        model.setPricePerDay(new BigDecimal("100.00"));
        model.setImage("url");
        Category category = new Category();
        category.setName("Sedán");
        model.setCategory(category);

        Favorite favorite = new Favorite();
        favorite.setId(5L);
        favorite.setUser(client);
        favorite.setCarModel(model);
        favorite.setCreatedAt(LocalDateTime.now());

        when(favoriteRepository.findByUserOrderByCreatedAtDesc(client)).thenReturn(List.of(favorite));
        when(carRepository.countAvailableByModel(model.getId())).thenReturn(2L);
        when(carRepository.countByCarModelId(model.getId())).thenReturn(3L);

        ResponseEntity<?> response = controller.getMyFavorites(principal);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) response.getBody()).get("count")).isEqualTo(1);

        when(carModelRepository.findById(model.getId())).thenReturn(Optional.of(model));
        when(favoriteRepository.existsByUserIdAndCarModelId(client.getId(), model.getId())).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite saved = invocation.getArgument(0);
            saved.setId(6L);
            return saved;
        });
        ResponseEntity<?> addResponse = controller.addToFavorites(Map.of("carModelId", model.getId()), principal);
        assertThat(addResponse.getStatusCodeValue()).isEqualTo(200);

        when(favoriteRepository.findByUserAndCarModel(client, model)).thenReturn(Optional.of(favorite));
        ResponseEntity<?> removeResponse = controller.removeFromFavorites(model.getId(), principal);
        assertThat(removeResponse.getStatusCodeValue()).isEqualTo(200);

        when(favoriteRepository.existsByUserIdAndCarModelId(client.getId(), model.getId())).thenReturn(true);
        ResponseEntity<?> checkResponse = controller.isFavorite(model.getId(), principal);
        assertThat(checkResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) checkResponse.getBody()).get("isFavorite")).isEqualTo(true);

        when(favoriteRepository.findFavoriteCarModelIdsByUserId(client.getId())).thenReturn(Set.of(model.getId()));
        ResponseEntity<?> idsResponse = controller.getFavoriteIds(principal);
        assertThat(idsResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) idsResponse.getBody()).get("count")).isEqualTo(1);
    }

    @Test
    void reservationControllerUsesHelperResponsesAndCancelPaths() {
        ReservationController controller = new ReservationController(reservationRepository, carRepository, carModelRepository, userRepository, branchRepository);
        User client = createUser(30L, "cliente3@example.com", "Cliente", "Tres", "CLIENT");
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(client);
        reservation.setCarModel(new CarModel());
        reservation.getCarModel().setId(100L);
        reservation.getCarModel().setBrand("Honda");
        reservation.getCarModel().setModel("Civic");
        reservation.getCarModel().setYear(2023);
        reservation.getCarModel().setPricePerDay(new BigDecimal("75.00"));
        reservation.getCarModel().setImage("img");
        Category category = new Category();
        category.setName("Compacto");
        reservation.getCarModel().setCategory(category);
        reservation.setStartDate(LocalDate.now().minusDays(1));
        reservation.setEndDate(LocalDate.now().plusDays(1));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        reservation.setTotalAmount(new BigDecimal("150.00"));
        reservation.setTotalDays(2);
        reservation.setPricePerDay(new BigDecimal("75.00"));
        reservation.setPickupBranch(new Branch());
        reservation.getPickupBranch().setName("Sede 1");
        reservation.setDropoffBranch(new Branch());
        reservation.getDropoffBranch().setName("Sede 2");

        when(reservationRepository.findByUserOrderByStartDateDesc(client)).thenReturn(List.of(reservation));
        ResponseEntity<?> myReservations = controller.getMyReservations(principal);
        assertThat(myReservations.getStatusCodeValue()).isEqualTo(200);
        assertThat(((Map<?, ?>) myReservations.getBody()).get("count")).isEqualTo(1);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        ResponseEntity<?> byIdResponse = controller.getReservationById(1L, principal);
        assertThat(byIdResponse.getStatusCodeValue()).isEqualTo(200);

        ReservationRequest request = new ReservationRequest();
        request.setCarId(100L);
        request.setPickupBranchId(1L);
        request.setDropoffBranchId(2L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        when(carModelRepository.findById(100L)).thenReturn(Optional.of(reservation.getCarModel()));
        Branch pickup = new Branch(); pickup.setId(1L); pickup.setName("Sede 1");
        Branch dropoff = new Branch(); dropoff.setId(2L); dropoff.setName("Sede 2");
        when(branchRepository.findById(1L)).thenReturn(Optional.of(pickup));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(dropoff));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        ResponseEntity<?> createResponse = controller.createReservation(request, principal);
        assertThat(createResponse.getStatusCodeValue()).isEqualTo(200);

        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(3L);
        confirmedReservation.setUser(client);
        confirmedReservation.setStatus(ReservationStatus.PENDING);
        confirmedReservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        Car availableCar = new Car();
        availableCar.setId(77L);
        availableCar.setPlate("ABC-123");
        availableCar.setStatus(CarStatus.AVAILABLE);
        CarModel availableCarModel = new CarModel();
        availableCarModel.setCategory(new Category() {{ setName("Compacto"); }});
        availableCar.setCarModel(availableCarModel);
        CarModel carModel = new CarModel();
        carModel.setId(100L);
        confirmedReservation.setCarModel(carModel);
        confirmedReservation.setStartDate(LocalDate.now().plusDays(1));
        confirmedReservation.setEndDate(LocalDate.now().plusDays(2));
        when(reservationRepository.findById(3L)).thenReturn(Optional.of(confirmedReservation));
        when(carRepository.findAvailableUnitForModel(any(), any(), any()))
                .thenReturn(List.of(availableCar));
        ResponseEntity<?> confirmResponse = controller.confirmPayment(3L, principal);
        assertThat(confirmResponse.getStatusCodeValue()).isEqualTo(200);

        Reservation confirmedForCancel = new Reservation();
        confirmedForCancel.setId(4L);
        confirmedForCancel.setUser(client);
        confirmedForCancel.setStatus(ReservationStatus.CONFIRMED);
        confirmedForCancel.setPaymentStatus(PaymentStatus.PAID);
        confirmedForCancel.setStartDate(LocalDate.now().plusDays(10));
        Car selectedCar = new Car();
        selectedCar.setId(88L);
        selectedCar.setPlate("XYZ-999");
        selectedCar.setStatus(CarStatus.RENTED);
        confirmedForCancel.setCar(selectedCar);
        when(reservationRepository.findById(4L)).thenReturn(Optional.of(confirmedForCancel));
        ResponseEntity<?> cancelResponse = controller.cancelReservation(4L, principal);
        assertThat(cancelResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(selectedCar.getStatus()).isEqualTo(CarStatus.AVAILABLE);
        verify(carRepository).save(selectedCar);
    }
}
