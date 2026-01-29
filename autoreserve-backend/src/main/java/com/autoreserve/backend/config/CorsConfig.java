package com.autoreserve.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

/**
 * Configuración de Intercambio de Recursos de Origen Cruzado (CORS).
 * Define las políticas de seguridad para permitir peticiones desde aplicaciones externas.
 */
@Configuration
public class CorsConfig {

    /**
     * Define la fuente de configuración CORS para la aplicación.
     * Establece los permisos sobre orígenes, métodos, cabeceras y credenciales.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Especifica los orígenes permitidos (URL del frontend en desarrollo)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173/"
        ));

        // Define los métodos HTTP autorizados para las peticiones
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Especifica las cabeceras permitidas en las peticiones entrantes
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        // Define las cabeceras que el cliente tiene permitido leer en la respuesta
        config.setExposedHeaders(List.of(
                "Authorization"
        ));

        // Permite el envío de cookies o cabeceras de autenticación en peticiones cruzadas
        config.setAllowCredentials(true);

        // Registra la configuración para todas las rutas del sistema
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
