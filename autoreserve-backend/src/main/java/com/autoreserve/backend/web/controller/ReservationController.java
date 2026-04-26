package com.autoreserve.backend.web.controller;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.PaymentStatus;
import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarModelRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.reservation.ReservationRequest;
import com.autoreserve.backend.dto.reservation.ReservationResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public ReservationController(ReservationRepository reservationRepository,
            CarRepository carRepository,
            CarModelRepository carModelRepository,
            UserRepository userRepository,
            BranchRepository branchRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations(
            @AuthenticationPrincipal UserDetails principal) {

        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Reservation> reservations = reservationRepository.findByUserOrderByStartDateDesc(user);

            List<ReservationResponse> responses = reservations.stream()
                    .map(reservation -> new ReservationResponse(
                    reservation.getId(),
                    reservation.getCar().getId(),
                    reservation.getCar().getCarModel().getBrand(),
                    reservation.getCar().getCarModel().getModel(),
                    reservation.getCar().getCarModel().getYear(),
                    reservation.getCar().getCarModel().getImage(),
                    reservation.getCar().getCarModel().getCategory().getName(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getStatus().name(),
                    reservation.getPaymentStatus().name(),
                    reservation.getTotalAmount(),
                    reservation.getTotalDays(),
                    reservation.getPricePerDay(),
                    reservation.getPickupBranch() != null ? reservation.getPickupBranch().getName() : "No especificada",
                    reservation.getDropoffBranch() != null ? reservation.getDropoffBranch().getName() : "No especificada",
                    reservation.getUser().getId(),
                    reservation.getUser().getFirstName(),
                    reservation.getUser().getLastName(),
                    reservation.getUser().getEmail()
            ))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reservas obtenidas exitosamente",
                    "data", responses
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            // Verificar que la reserva pertenece al usuario
            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No tienes permisos para ver esta reserva"));
            }

            ReservationResponse response = new ReservationResponse(
                    reservation.getId(),
                    reservation.getCar().getId(),
                    reservation.getCar().getCarModel().getBrand(),
                    reservation.getCar().getCarModel().getModel(),
                    reservation.getCar().getCarModel().getYear(),
                    reservation.getCar().getCarModel().getImage(),
                    reservation.getCar().getCarModel().getCategory().getName(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getStatus().name(),
                    reservation.getPaymentStatus().name(),
                    reservation.getTotalAmount(),
                    reservation.getTotalDays(),
                    reservation.getPricePerDay(),
                    reservation.getPickupBranch() != null ? reservation.getPickupBranch().getName() : "No especificada",
                    reservation.getDropoffBranch() != null ? reservation.getDropoffBranch().getName() : "No especificada",
                    reservation.getUser().getId(),
                    reservation.getUser().getFirstName(),
                    reservation.getUser().getLastName(),
                    reservation.getUser().getEmail()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reserva obtenida exitosamente",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Branch pickupBranch = branchRepository.findById(request.getPickupBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de retiro no encontrada"));

            Branch dropoffBranch = branchRepository.findById(request.getDropoffBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de entrega no encontrada"));

            // Validar fechas
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (days < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Las fechas de reserva son inválidas"));
            }

            // Buscar una unidad disponible del modelo solicitado
            List<Car> availableUnits = carRepository.findAvailableUnitForModel(
                    request.getCarId(), request.getStartDate(), request.getEndDate());

            if (availableUnits.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No hay unidades disponibles para ese modelo en las fechas seleccionadas"));
            }

            Car car = availableUnits.get(0);

            long billableDays = Math.max(days, 1);
            BigDecimal totalAmount = car.getPricePerDay().multiply(BigDecimal.valueOf(billableDays));

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setCar(car);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setPaymentStatus(PaymentStatus.PENDING);
            reservation.setTotalAmount(totalAmount);
            reservation.setTotalDays((int) billableDays);
            reservation.setPricePerDay(car.getPricePerDay());
            reservation.setPickupBranch(pickupBranch);
            reservation.setDropoffBranch(dropoffBranch);

            Reservation saved = reservationRepository.save(reservation);

            ReservationResponse response = new ReservationResponse(
                    saved.getId(),
                    saved.getCar().getId(),
                    saved.getCar().getBrand(),
                    saved.getCar().getModelName(),
                    saved.getCar().getYear(),
                    saved.getCar().getImage(),
                    saved.getCar().getCategory().getName(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    saved.getStatus().name(),
                    saved.getPaymentStatus().name(),
                    saved.getTotalAmount(),
                    saved.getTotalDays(),
                    saved.getPricePerDay(),
                    saved.getPickupBranch().getName(),
                    saved.getDropoffBranch().getName(),
                    saved.getUser().getId(),
                    saved.getUser().getFirstName(),
                    saved.getUser().getLastName(),
                    saved.getUser().getEmail()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reserva creada exitosamente",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    // Confirmar pago: cambia PENDING -> CONFIRMED y paymentStatus -> PAID
    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No tienes permisos para confirmar esta reserva"));
            }

            if (reservation.getStatus() != ReservationStatus.PENDING) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Solo se pueden confirmar reservas en estado pendiente"));
            }

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setPaymentStatus(PaymentStatus.PAID);
            reservationRepository.save(reservation);

            ReservationResponse response = new ReservationResponse(
                    reservation.getId(),
                    reservation.getCar().getId(),
                    reservation.getCar().getCarModel().getBrand(),
                    reservation.getCar().getCarModel().getModel(),
                    reservation.getCar().getCarModel().getYear(),
                    reservation.getCar().getCarModel().getImage(),
                    reservation.getCar().getCarModel().getCategory().getName(),
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getStatus().name(),
                    reservation.getPaymentStatus().name(),
                    reservation.getTotalAmount(),
                    reservation.getTotalDays(),
                    reservation.getPricePerDay(),
                    reservation.getPickupBranch() != null ? reservation.getPickupBranch().getName() : "No especificada",
                    reservation.getDropoffBranch() != null ? reservation.getDropoffBranch().getName() : "No especificada",
                    reservation.getUser().getId(),
                    reservation.getUser().getFirstName(),
                    reservation.getUser().getLastName(),
                    reservation.getUser().getEmail()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pago confirmado exitosamente",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No tienes permisos para cancelar esta reserva"));
            }

            // Se puede cancelar si está PENDING o CONFIRMED
            if (reservation.getStatus() != ReservationStatus.PENDING
                    && reservation.getStatus() != ReservationStatus.CONFIRMED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Solo se pueden cancelar reservas pendientes o confirmadas"));
            }

            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reserva cancelada exitosamente",
                    "reservationId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}
