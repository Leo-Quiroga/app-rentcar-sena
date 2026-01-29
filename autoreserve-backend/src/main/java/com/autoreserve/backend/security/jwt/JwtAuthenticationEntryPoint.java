package com.autoreserve.backend.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Manejador de excepciones de autenticación.
 * Se encarga de capturar peticiones no autorizadas y retornar un error HTTP 401.
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws java.io.IOException {
        // Retorna un error 401 cuando un usuario intenta acceder a un recurso protegido sin credenciales válidas
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}