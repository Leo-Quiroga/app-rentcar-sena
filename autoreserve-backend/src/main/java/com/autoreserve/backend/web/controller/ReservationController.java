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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal UserDetails principal) {
        try {
            System.out.println("=== OBTENIENDO MIS RESERVAS ===");
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            System.out.println("Usuario: " + user.getEmail());
            
            List<Reservation> reservations = reservationRepository.findByUserOrderByStartDateDesc(user);
            System.out.println("Reservas encontradas: " + reservations.size());
            
            List<ReservationResponse> responses = reservations.stream()
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
                "count", responses.size()
            ));
        } catch (Exception e) {
            System.err.println("Error en getMyReservations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Reserva obtenida exitosamente", 
                "data", toResponse(reservation)
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Crea reserva en estado PENDING sin asignar auto.
     * El auto solo se asigna al confirmar el pago.
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            // Log para debug
            System.out.println("=== CREANDO RESERVA ===");
            System.out.println("CarId recibido: " + request.getCarId());
            
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que el carId no sea 0 o null
            if (request.getCarId() == null || request.getCarId() == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "ID del modelo de auto es requerido y debe ser válido"));
            }

            CarModel carModel = carModelRepository.findById(request.getCarId())
                    .orElseThrow(() -> new RuntimeException("Modelo no encontrado con ID: " + request.getCarId()));
            
            System.out.println("CarModel encontrado: " + carModel.getBrand() + " " + carModel.getModel() + " (ID: " + carModel.getId() + ")");

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
            reservation.setCarModel(carModel);  // Esto debe guardar la relación correctamente
            // car = null intencionalmente hasta que se pague
            reservation.setStartDate(request.getStartDate());
            reservation.setEndDate(request.getEndDate());
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setPaymentStatus(PaymentStatus.NO_PAYMENT);
            reservation.setTotalAmount(totalAmount);
            reservation.setTotalDays((int) billableDays);
            reservation.setPricePerDay(carModel.getPricePerDay());
            reservation.setPickupBranch(pickupBranch);
            reservation.setDropoffBranch(dropoffBranch);

            System.out.println("Guardando reserva con CarModel ID: " + carModel.getId());
            Reservation saved = reservationRepository.save(reservation);
            System.out.println("Reserva guardada con ID: " + saved.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reserva creada. Tienes 24 horas para completar el pago. El auto se asignará al confirmar el pago.",
                    "data", toResponse(saved)
            ));
        } catch (RuntimeException e) {
            System.err.println("Error en createReservation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error interno en createReservation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Confirma el pago: busca una unidad disponible, la asigna
     * y cambia la reserva a CONFIRMED.
     * El auto permanece AVAILABLE hasta la fecha de inicio.
     */
    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }
            if (reservation.getStatus() != ReservationStatus.PENDING) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Solo se pueden confirmar reservas pendientes de pago"));
            }

            // Buscar unidad disponible del modelo en las fechas solicitadas
            List<Car> availableUnits = carRepository.findAvailableUnitForModel(
                    reservation.getCarModel().getId(),
                    reservation.getStartDate(),
                    reservation.getEndDate());

            if (availableUnits.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Lo sentimos, no hay unidades disponibles de este modelo para las fechas seleccionadas. El pago no puede procesarse."
                ));
            }

            // Asignar la primera unidad disponible - NO cambiar estado hasta fecha de inicio
            Car assignedCar = availableUnits.get(0);
            // El auto permanece AVAILABLE hasta que inicie la reserva
            
            reservation.setCar(assignedCar);
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setPaymentStatus(PaymentStatus.PAID);
            reservationRepository.save(reservation);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pago confirmado exitosamente. Tu reserva está confirmada. El auto se asignará el día de inicio.",
                    "data", toResponse(reservation)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Cancelar reserva:
     * - PENDING: se cancela libremente, paymentStatus = NO_PAYMENT
     * - CONFIRMED: solo si faltan 7 o más días para el inicio, paymentStatus = REFUND_PENDING
     * - IN_PROGRESS o COMPLETED: no se puede cancelar
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        try {
            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

            if (!reservation.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "Sin permisos"));
            }

            if (reservation.getStatus() == ReservationStatus.IN_PROGRESS) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No puedes cancelar una reserva que está en curso"));
            }
            if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No puedes cancelar una reserva completada"));
            }
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Esta reserva ya está cancelada"));
            }

            // Reserva CONFIRMED: verificar regla de 7 días
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getStartDate());
                if (daysUntilStart < 7) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "error", "Solo puedes cancelar una reserva confirmada con al menos 7 días de anticipación. Tu reserva inicia el " + reservation.getStartDate()
                    ));
                }
                // Liberar el auto asignado (debería estar AVAILABLE, pero asegurar)
                if (reservation.getCar() != null) {
                    reservation.getCar().setStatus(CarStatus.AVAILABLE);
                    carRepository.save(reservation.getCar());
                    System.out.println("🔓 Auto " + reservation.getCar().getPlate() + " liberado por cancelación de reserva " + id);
                }
                reservation.setPaymentStatus(PaymentStatus.REFUND_PENDING);
            }

            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);

            String message = reservation.getPaymentStatus() == PaymentStatus.REFUND_PENDING
                    ? "Reserva cancelada. Tu reembolso está en proceso."
                    : "Reserva cancelada exitosamente.";

            return ResponseEntity.ok(Map.of("success", true, "message", message, "reservationId", id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
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
