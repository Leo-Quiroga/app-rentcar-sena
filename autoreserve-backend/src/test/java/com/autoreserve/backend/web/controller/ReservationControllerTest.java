package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.reservation.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private UserDetails principal;

    private ReservationController controller;
    private User client;
    private CarModel carModel;
    private Branch branch;
    private Reservation existingReservation;

    @BeforeEach
    void setUp() {
        controller = new ReservationController(reservationRepository, carRepository, carModelRepository, userRepository, branchRepository);

        Role clientRole = new Role();
        clientRole.setId(1L);
        clientRole.setName("CLIENT");

        client = new User();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setFirstName("Client");
        client.setLastName("Example");
        client.setRole(clientRole);

        branch = new Branch();
        branch.setId(1L);
        branch.setName("Central");

        carModel = new CarModel();
        carModel.setId(10L);
        carModel.setBrand("Toyota");
        carModel.setModel("Corolla");
        carModel.setYear(2024);
        carModel.setPricePerDay(new BigDecimal("40.00"));
        carModel.setCategory(new Category());

        existingReservation = new Reservation();
        existingReservation.setId(1L);
        existingReservation.setUser(client);
        existingReservation.setCarModel(carModel);
        existingReservation.setStartDate(LocalDate.of(2025, 2, 1));
        existingReservation.setEndDate(LocalDate.of(2025, 2, 3));
        existingReservation.setStatus(ReservationStatus.PENDING);
        existingReservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        existingReservation.setTotalAmount(new BigDecimal("80.00"));
        existingReservation.setTotalDays(2);
        existingReservation.setPricePerDay(new BigDecimal("40.00"));
        existingReservation.setPickupBranch(branch);
        existingReservation.setDropoffBranch(branch);
    }

    @Test
    void getMyReservations_ReturnsReservationsForUser() {
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findByUserOrderByStartDateDesc(client)).thenReturn(List.of(existingReservation));

        ResponseEntity<?> response = controller.getMyReservations(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("count")).isEqualTo(1);
    }

    @Test
    void getMyReservations_UserNotFound_ReturnsInternalServerError() {
        when(principal.getUsername()).thenReturn("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getMyReservations(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getReservationById_ReturnsForbiddenWhenUserDoesNotOwnReservation() {
        User other = new User();
        other.setId(2L);
        existingReservation.setUser(other);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.getReservationById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Sin permisos");
    }

    @Test
    void createReservation_ReturnsBadRequestWhenCarIdIsNull() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(null);
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 2));
        request.setPickupBranchId(1L);
        request.setDropoffBranchId(1L);

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        ResponseEntity<?> response = controller.createReservation(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("ID del modelo de auto es requerido y debe ser válido");
    }

    @Test
    void createReservation_ReturnsBadRequestWhenCarModelMissing() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(999L);
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 2));
        request.setPickupBranchId(1L);
        request.setDropoffBranchId(1L);

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(carModelRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.createReservation(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Modelo no encontrado con ID: 999");
    }

    @Test
    void createReservation_ReturnsOkWhenValid() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(carModel.getId());
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 2));
        request.setPickupBranchId(branch.getId());
        request.setDropoffBranchId(branch.getId());

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(carModelRepository.findById(carModel.getId())).thenReturn(Optional.of(carModel));
        when(branchRepository.findById(branch.getId())).thenReturn(Optional.of(branch));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingReservation);

        ResponseEntity<?> response = controller.createReservation(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
    }

    @Test
    void createReservation_ReturnsBadRequestWhenPickupBranchMissing() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(carModel.getId());
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 2));
        request.setPickupBranchId(999L);
        request.setDropoffBranchId(branch.getId());

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(carModelRepository.findById(carModel.getId())).thenReturn(Optional.of(carModel));
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.createReservation(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createReservation_ReturnsBadRequestWhenDropoffBranchMissing() {
        ReservationRequest request = new ReservationRequest();
        request.setCarId(carModel.getId());
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 2));
        request.setPickupBranchId(branch.getId());
        request.setDropoffBranchId(999L);

        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(carModelRepository.findById(carModel.getId())).thenReturn(Optional.of(carModel));
        when(branchRepository.findById(branch.getId())).thenReturn(Optional.of(branch));
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.createReservation(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void confirmPayment_ReturnsBadRequestWhenReservationNotPending() {
        existingReservation.setStatus(ReservationStatus.CONFIRMED);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.confirmPayment(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Solo se pueden confirmar reservas pendientes de pago");
    }

    @Test
    void confirmPayment_ReturnsForbiddenWhenUserMismatch() {
        User other = new User();
        other.setId(2L);
        other.setEmail("other@example.com");
        existingReservation.setUser(other);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.confirmPayment(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Sin permisos");
    }

    @Test
    void cancelReservation_ReturnsRefundPendingWhenConfirmedPaid() {
        Car assignedCar = new Car();
        assignedCar.setId(123L);
        assignedCar.setStatus(CarStatus.AVAILABLE);
        assignedCar.setCarModel(carModel);
        existingReservation.setStatus(ReservationStatus.CONFIRMED);
        existingReservation.setPaymentStatus(PaymentStatus.PAID);
        existingReservation.setCar(assignedCar);
        existingReservation.setStartDate(LocalDate.now().plusDays(10));
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));
        when(carRepository.save(any(Car.class))).thenReturn(assignedCar);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingReservation);

        ResponseEntity<?> response = controller.cancelReservation(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
    }

    @Test
    void cancelReservation_ReturnsBadRequestWhenNoPermission() {
        User other = new User();
        other.setId(2L);
        existingReservation.setUser(other);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.cancelReservation(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Sin permisos");
    }

    @Test
    void cancelReservation_ReturnsBadRequestWhenAlreadyCancelled() {
        existingReservation.setStatus(ReservationStatus.CANCELLED);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.cancelReservation(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Esta reserva ya está cancelada");
    }

    @Test
    void cancelReservation_ReturnsBadRequestWhenInProgress() {
        existingReservation.setStatus(ReservationStatus.PENDING);
        existingReservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));
        when(carRepository.findAvailableUnitForModel(carModel.getId(), existingReservation.getStartDate(), existingReservation.getEndDate()))
                .thenReturn(List.of());

        ResponseEntity<?> response = controller.confirmPayment(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("Lo sentimos, no hay unidades disponibles de este modelo para las fechas seleccionadas. El pago no puede procesarse.");
    }

    @Test
    void confirmPayment_ReturnsSuccessWhenAvailableCar() {
        Car available = new Car();
        available.setId(99L);
        available.setStatus(CarStatus.AVAILABLE);
        available.setCarModel(carModel);
        available.setPlate("ABC-999");

        existingReservation.setStatus(ReservationStatus.PENDING);
        existingReservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));
        when(carRepository.findAvailableUnitForModel(carModel.getId(), existingReservation.getStartDate(), existingReservation.getEndDate()))
                .thenReturn(List.of(available));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.confirmPayment(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
    }

    @Test
    void cancelReservation_ReturnsBadRequestWhenCompleted() {
        existingReservation.setStatus(ReservationStatus.COMPLETED);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));

        ResponseEntity<?> response = controller.cancelReservation(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("error")).isEqualTo("No puedes cancelar una reserva completada");
    }

    @Test
    void cancelReservation_ReturnsOkWhenPending() {
        existingReservation.setStatus(ReservationStatus.PENDING);
        when(principal.getUsername()).thenReturn(client.getEmail());
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingReservation);

        ResponseEntity<?> response = controller.cancelReservation(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((java.util.Map<?, ?>) response.getBody()).get("success")).isEqualTo(true);
    }
}
