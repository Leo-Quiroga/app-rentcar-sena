package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.reservation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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
                            reservation.getCar().getModel(),
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
            
            Branch pickupBranch = branchRepository.findById(request.getPickupBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de retiro no encontrada"));
            
            Branch dropoffBranch = branchRepository.findById(request.getDropoffBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de entrega no encontrada"));
            
            // Calcular total
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (days <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Las fechas de reserva son inválidas"));
            }
            
            BigDecimal totalAmount = car.getPricePerDay().multiply(BigDecimal.valueOf(days));
            
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setCar(car);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setPaymentStatus(PaymentStatus.PENDING);
            reservation.setTotalAmount(totalAmount);
            reservation.setTotalDays((int) days);
            reservation.setPricePerDay(car.getPricePerDay());
            reservation.setPickupBranch(pickupBranch);
            reservation.setDropoffBranch(dropoffBranch);
            
            Reservation saved = reservationRepository.save(reservation);
            
            ReservationResponse response = new ReservationResponse(
                    saved.getId(),
                    saved.getCar().getId(),
                    saved.getCar().getBrand(),
                    saved.getCar().getModel(),
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