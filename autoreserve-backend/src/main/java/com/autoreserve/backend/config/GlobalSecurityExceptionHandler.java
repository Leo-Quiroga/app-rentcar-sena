package com.autoreserve.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controlador de asesoría global para la gestión de excepciones de seguridad.
 * Captura y centraliza los errores de autorización lanzados por Spring Security.
 */
@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    /**
     * Maneja las excepciones de tipo AccessDeniedException.
     * Se activa cuando un usuario autenticado intenta acceder a un recurso para el cual no tiene permisos.
     * * @param ex La excepción de acceso denegado capturada.
     * @return ResponseEntity con código de estado 403 (Forbidden) y mensaje descriptivo.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Forbidden");
    }
}
