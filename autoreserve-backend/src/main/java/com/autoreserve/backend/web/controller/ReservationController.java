package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.*;
import com.autoreserve.backend.domain.repository.*;
import com.autoreserve.backend.dto.reservation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public ReservationController(ReservationRepository reservationRepository, 
                               CarRepository carRepository, 
                               UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Reservation> reservations = reservationRepository.findByUserOrderByStartDateDesc(user);
        
        List<ReservationResponse> responses = reservations.stream()
                .map(reservation -> new ReservationResponse(
                        reservation.getId(),
                        reservation.getCar().getId(),
                        reservation.getCar().getBrand(),
                        reservation.getCar().getModel(),
                        reservation.getStartDate(),
                        reservation.getEndDate(),
                        reservation.getStatus().name(),
                        reservation.getTotalAmount(),
                        reservation.getCar().getBranch().getName()
                ))
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // Verificar que la reserva pertenece al usuario
        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        ReservationResponse response = new ReservationResponse(
                reservation.getId(),
                reservation.getCar().getId(),
                reservation.getCar().getBrand(),
                reservation.getCar().getModel(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus().name(),
                reservation.getTotalAmount(),
                reservation.getCar().getBranch().getName()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
        
        // Verificar disponibilidad del auto
        if (car.getStatus() != CarStatus.AVAILABLE) {
            return ResponseEntity.badRequest().build();
        }
        
        // Calcular total
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        BigDecimal totalAmount = car.getPricePerDay().multiply(BigDecimal.valueOf(days));
        
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotalAmount(totalAmount);
        
        Reservation saved = reservationRepository.save(reservation);
        
        ReservationResponse response = new ReservationResponse(
                saved.getId(),
                saved.getCar().getId(),
                saved.getCar().getBrand(),
                saved.getCar().getModel(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getStatus().name(),
                saved.getTotalAmount(),
                saved.getCar().getBranch().getName()
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // Verificar que la reserva pertenece al usuario
        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return ResponseEntity.badRequest()
                    .body("Solo se pueden cancelar reservas confirmadas");
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        return ResponseEntity.ok("Reserva cancelada correctamente");
    }
}