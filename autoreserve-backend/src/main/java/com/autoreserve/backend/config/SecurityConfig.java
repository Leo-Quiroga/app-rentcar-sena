package com.autoreserve.backend.config;

import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.security.jwt.JwtAuthenticationFilter;
import com.autoreserve.backend.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad de la aplicación.
 * Define la cadena de filtros de seguridad, políticas de acceso, gestión de sesiones y encriptación.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Constructor para la inyección de servicios de JWT y repositorios de usuario.
     */
    public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Define el bean para la codificación de contraseñas utilizando el algoritmo BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de la cadena de filtros de seguridad (Security Filter Chain).
     * Establece las reglas de autorización, desactivación de CSRF y gestión de excepciones.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Inicialización del filtro de autenticación basado en tokens JWT
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userRepository);

        http
                .cors(cors -> {}) // Habilita la configuración de CORS definida en el proyecto
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF al tratarse de una API stateless
                .exceptionHandling(ex -> ex
                        // Manejo de errores para peticiones no autenticadas (401)
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("--- DEBUG: 401 Unauthorized en: " + request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        // Manejo de errores para peticiones sin permisos suficientes (403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("--- DEBUG: 403 Forbidden en: " + request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )
                .sessionManagement(session ->
                        // Define una política de sesión sin estado (Stateless)
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Define rutas públicas exentas de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Cualquier otra petición requiere autenticación previa
                        .anyRequest().authenticated()
                )
                // Inserta el filtro JWT antes del filtro estándar de autenticación de usuario y contraseña
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    /**
     * Expone el AuthenticationManager para ser utilizado en el proceso de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}