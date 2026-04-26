package com.autoreserve.backend.web.controller;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.entity.Car;
import com.autoreserve.backend.domain.entity.CarStatus;
import com.autoreserve.backend.domain.entity.PaymentStatus;
import com.autoreserve.backend.domain.entity.Reservation;
import com.autoreserve.backend.domain.entity.ReservationStatus;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.domain.repository.CarRepository;
import com.autoreserve.backend.domain.repository.ReservationRepository;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.reservation.AdminReservationRequest;
import com.autoreserve.backend.dto.reservation.ReservationResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    private final BranchRepository branchRepository;

    public AdminReservationController(ReservationRepository reservationRepository, 
                                    CarRepository carRepository, 
                                    UserRepository userRepository,
                                    BranchRepository branchRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Page<Reservation> reservationPage = reservationRepository.findAll(PageRequest.of(page, size));
            
            List<ReservationResponse> responses = reservationPage.getContent().stream()
                    .map(reservation -> new ReservationResponse(
                            reservation.getId(),
                            reservation.getCar().getId(),
                            reservation.getCar().getBrand(),
                            reservation.getCar().getModelName(),
                            reservation.getCar().getYear(),
                            reservation.getCar().getImage(),
                            reservation.getCar().getCategory().getName(),
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createReservationForClient(@Valid @RequestBody AdminReservationRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));
            
            Car car = carRepository.findById(request.getCarId())
                    .orElseThrow(() -> new RuntimeException("Auto no encontrado con ID: " + request.getCarId()));
            
            // Verificar disponibilidad del auto
            if (car.getStatus() != CarStatus.AVAILABLE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El auto no está disponible para reserva"));
            }

            // Verificar que no exista reserva activa para ese auto en las fechas solicitadas
            if (reservationRepository.existsOverlappingReservation(car.getId(), request.getStartDate(), request.getEndDate())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El auto ya tiene una reserva para las fechas seleccionadas"));
            }
            
            Branch pickupBranch = branchRepository.findById(request.getPickupBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de retiro no encontrada"));
            
            Branch dropoffBranch = branchRepository.findById(request.getDropoffBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de entrega no encontrada"));
            
            // Calcular total
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (days < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Las fechas de reserva son inválidas"));
            }
            
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
                    "message", "Reserva creada exitosamente para el cliente",
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

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
            
            ReservationStatus newStatus = ReservationStatus.valueOf(status.toUpperCase());
            reservation.setStatus(newStatus);
            reservationRepository.save(reservation);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Estado de reserva actualizado exitosamente",
                    "reservationId", id,
                    "newStatus", newStatus.name()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Estado de reserva inválido: " + status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}