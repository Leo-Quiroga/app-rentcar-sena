package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.repository.*;
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
     * Endpoint de prueba para verificar la correcta configuración del RBAC (Control de Acceso Basado en Roles).
     * Requiere que el usuario autenticado posea el rol 'ADMIN'.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Welcome ADMIN, RBAC is working!";
    }

    /**
     * Estadísticas del dashboard administrativo
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getStats() {
        long totalUsers = userRepository.count();
        long totalCars = carRepository.count();
        long totalReservations = reservationRepository.count();
        long totalCategories = categoryRepository.count();
        long totalBranches = branchRepository.count();

        return Map.of(
                "totalUsers", totalUsers,
                "totalCars", totalCars,
                "totalReservations", totalReservations,
                "totalCategories", totalCategories,
                "totalBranches", totalBranches
        );
    }
}