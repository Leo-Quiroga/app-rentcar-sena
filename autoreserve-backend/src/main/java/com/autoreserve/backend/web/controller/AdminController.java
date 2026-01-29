package com.autoreserve.backend.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador administrativo para funciones restringidas del sistema.
 * Utiliza anotaciones de seguridad para asegurar que solo usuarios con privilegios elevados accedan.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Endpoint de prueba para verificar la correcta configuración del RBAC (Control de Acceso Basado en Roles).
     * Requiere que el usuario autenticado posea el rol 'ADMIN'.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Welcome ADMIN, RBAC is working!";
    }
}