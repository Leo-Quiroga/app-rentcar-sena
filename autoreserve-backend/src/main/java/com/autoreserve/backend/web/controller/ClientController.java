package com.autoreserve.backend.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para funciones específicas del cliente.
 * Protege los recursos asegurando que el usuario tenga el rol CLIENT.
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {

    /**
     * Endpoint de acceso restringido para clientes.
     * Incluye trazas de depuración para verificar la propagación del contexto de seguridad.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CLIENT')")
    public String clientDashboard() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        System.out.println("--- PASO 2: LLEGADA AL CONTROLADOR ---");
        System.out.println("Usuario actual: " + auth.getName());
        System.out.println("Autoridades actuales: " + auth.getAuthorities());

        return "Welcome CLIENT, RBAC is working!";
    }
}