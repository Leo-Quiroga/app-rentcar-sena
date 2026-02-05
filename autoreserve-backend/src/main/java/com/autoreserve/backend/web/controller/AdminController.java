package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador administrativo para funciones restringidas del sistema.
 * Utiliza anotaciones de seguridad para asegurar que solo usuarios con privilegios elevados accedan.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;

    public AdminController(UserRepository userRepository, CarRepository carRepository, 
                          ReservationRepository reservationRepository, CategoryRepository categoryRepository,
                          BranchRepository branchRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
    }

    /**
     * Estadísticas del dashboard administrativo
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            long totalUsers = userRepository.count();
            long totalCars = carRepository.count();
            long totalReservations = reservationRepository.count();
            long totalCategories = categoryRepository.count();
            long totalBranches = branchRepository.count();

            Map<String, Object> stats = Map.of(
                    "totalUsers", totalUsers,
                    "totalCars", totalCars,
                    "totalReservations", totalReservations,
                    "totalCategories", totalCategories,
                    "totalBranches", totalBranches,
                    "message", "Estadísticas obtenidas exitosamente"
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener las estadísticas", "details", e.getMessage()));
        }
    }
}