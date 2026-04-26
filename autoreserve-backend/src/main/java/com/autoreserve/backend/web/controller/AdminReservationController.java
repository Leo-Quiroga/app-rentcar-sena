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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public AdminReservationController(ReservationRepository reservationRepository,
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

    @GetMapping
    public ResponseEntity<?> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            System.out.println("=== OBTENIENDO RESERVAS ADMIN ===");
            Page<Reservation> reservationPage = reservationRepository.findAll(PageRequest.of(page, size));
            System.out.println("Reservas encontradas: " + reservationPage.getTotalElements());
            
            List<ReservationResponse> responses = reservationPage.getContent().stream()
                    .map(reservation -> {
                        try {
                            return toResponse(reservation);
                        } catch (Exception e) {
                            System.err.println("Error procesando reserva " + reservation.getId() + ": " + e.getMessage());
                            return null; // Filtrar reservas problemáticas
                        }
                    })
                    .filter(response -> response != null) // Remover nulls
                    .toList();
                    
            System.out.println("Respuestas procesadas: " + responses.size());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Reservas obtenidas exitosamente", 
                "data", responses,
                "totalElements", reservationPage.getTotalElements(),
                "totalPages", reservationPage.getTotalPages(),
                "processedCount", responses.size()
            ));
        } catch (Exception e) {
            System.err.println("Error en getAllReservations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Endpoint para que el admin vea el detalle de cualquier reserva (fix botón Ver) */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Reserva obtenida exitosamente", 
                "data", toResponse(reservation)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Admin crea reserva para un cliente — también en PENDING sin auto */
    @PostMapping
    public ResponseEntity<?> createReservationForClient(@Valid @RequestBody AdminReservationRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));

            CarModel carModel = carModelRepository.findById(request.getCarId())
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + request.getCarId()));

            Branch pickupBranch = branchRepository.findById(request.getPickupBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de retiro no encontrada"));
            Branch dropoffBranch = branchRepository.findById(request.getDropoffBranchId())
                    .orElseThrow(() -> new RuntimeException("Sede de entrega no encontrada"));

            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (days < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Las fechas de reserva son inválidas"));
            }

            long billableDays = Math.max(days, 1);
            BigDecimal totalAmount = carModel.getPricePerDay().multiply(BigDecimal.valueOf(billableDays));

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setCarModel(carModel);
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
            reservation.setTotalAmount(totalAmount);
            reservation.setTotalDays((int) billableDays);
            reservation.setPricePerDay(carModel.getPricePerDay());
            reservation.setPickupBranch(pickupBranch);
            reservation.setDropoffBranch(dropoffBranch);

            Reservation saved = reservationRepository.save(reservation);
            return ResponseEntity.ok(Map.of("success", true, "message", "Reserva creada exitosamente para el cliente", "data", toResponse(saved)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /** Admin cambia estado de reserva con lógica de negocio */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            ReservationStatus newStatus = ReservationStatus.valueOf(status.toUpperCase());
            ReservationStatus oldStatus = reservation.getStatus();

            // VALIDACIÓN ESTRICTA: Si cambia de PENDING a CONFIRMED
            if (oldStatus == ReservationStatus.PENDING && newStatus == ReservationStatus.CONFIRMED) {
                // 1. Verificar que el pago esté confirmado PRIMERO
                if (reservation.getPaymentStatus() != PaymentStatus.PAID) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false, 
                        "error", "No se puede confirmar una reserva sin pago. Debe cambiar PRIMERO el estado de pago a PAID."
                    ));
                }
                
                // 2. Buscar auto disponible del modelo para las fechas
                List<Car> availableUnits = carRepository.findAvailableUnitForModel(
                        reservation.getCarModel().getId(),
                        reservation.getStartDate(),
                        reservation.getEndDate());

                // 3. Si no hay autos disponibles, BLOQUEAR el cambio
                if (availableUnits.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "error", "No se puede confirmar la reserva: No hay unidades disponibles del modelo " + 
                                   reservation.getCarModel().getBrand() + " " + reservation.getCarModel().getModel() + 
                                   " para las fechas " + reservation.getStartDate() + " - " + reservation.getEndDate() + 
                                   ". La reserva permanece en estado PENDING."
                    ));
                }

                // 4. Asignar la primera unidad disponible y marcarla como RENTED
                Car assignedCar = availableUnits.get(0);
                assignedCar.setStatus(CarStatus.RENTED);
                carRepository.save(assignedCar);
                reservation.setCar(assignedCar);
                
                System.out.println("✅ Admin confirmó reserva " + id + ": Auto " + assignedCar.getPlate() + 
                                 " (ID: " + assignedCar.getId() + ") asignado y marcado como RENTED");
            }

            // Si cancela una reserva CONFIRMED, liberar el auto
            if (newStatus == ReservationStatus.CANCELLED && reservation.getCar() != null
                    && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.getCar().setStatus(CarStatus.AVAILABLE);
                carRepository.save(reservation.getCar());
                reservation.setPaymentStatus(PaymentStatus.REFUND_PENDING);
                System.out.println("🔓 Auto " + reservation.getCar().getPlate() + " liberado por cancelación de reserva " + id);
            }

            // Si completa, liberar el auto
            if (newStatus == ReservationStatus.COMPLETED && reservation.getCar() != null) {
                reservation.getCar().setStatus(CarStatus.AVAILABLE);
                carRepository.save(reservation.getCar());
                System.out.println("🔓 Auto " + reservation.getCar().getPlate() + " liberado por completar reserva " + id);
            }

            reservation.setStatus(newStatus);
            reservationRepository.save(reservation);

            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Estado actualizado exitosamente" + 
                          (reservation.getCar() != null ? ". Auto " + reservation.getCar().getPlate() + " asignado." : ""),
                "reservationId", id, 
                "newStatus", newStatus.name(),
                "assignedCarId", reservation.getCar() != null ? reservation.getCar().getId() : null
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Estado inválido: " + status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Admin edita el estado de pago manualmente */
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, @RequestParam String paymentStatus) {
        try {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
            PaymentStatus newPaymentStatus = PaymentStatus.valueOf(paymentStatus.toUpperCase());
            reservation.setPaymentStatus(newPaymentStatus);
            reservationRepository.save(reservation);
            return ResponseEntity.ok(Map.of("success", true, "message", "Estado de pago actualizado",
                    "reservationId", id, "newPaymentStatus", newPaymentStatus.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Estado de pago inválido: " + paymentStatus));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private ReservationResponse toResponse(Reservation r) {
        try {
            // Validar que carModel no sea null
            if (r.getCarModel() == null) {
                System.err.println("WARNING: Reserva " + r.getId() + " tiene carModel null");
                throw new RuntimeException("Reserva sin modelo de auto asignado");
            }
            
            String brand = r.getCar() != null ? r.getCar().getBrand() : r.getCarModel().getBrand();
            String model = r.getCar() != null ? r.getCar().getModelName() : r.getCarModel().getModel();
            Integer year  = r.getCar() != null ? r.getCar().getYear()  : r.getCarModel().getYear();
            String image  = r.getCar() != null ? r.getCar().getImage() : r.getCarModel().getImage();
            String cat    = r.getCar() != null ? r.getCar().getCategory().getName() : r.getCarModel().getCategory().getName();
            Long carId    = r.getCar() != null ? r.getCar().getId() : null;

            return new ReservationResponse(
                    r.getId(), carId, brand, model, year, image, cat,
                    r.getStartDate(), r.getEndDate(),
                    r.getStatus().name(), r.getPaymentStatus().name(),
                    r.getTotalAmount(), r.getTotalDays(), r.getPricePerDay(),
                    r.getPickupBranch() != null ? r.getPickupBranch().getName() : "No especificada",
                    r.getDropoffBranch() != null ? r.getDropoffBranch().getName() : "No especificada",
                    r.getUser().getId(), r.getUser().getFirstName(),
                    r.getUser().getLastName(), r.getUser().getEmail()
            );
        } catch (Exception e) {
            System.err.println("Error en toResponse para reserva " + r.getId() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error procesando datos de reserva: " + e.getMessage());
        }
    }
}
