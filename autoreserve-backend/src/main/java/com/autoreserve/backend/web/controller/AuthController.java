
package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.repository.RoleRepository;
import com.autoreserve.backend.dto.auth.LoginRequest;
import com.autoreserve.backend.dto.auth.LoginResponse;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.dto.auth.RegisterRequest;
import com.autoreserve.backend.security.jwt.JwtService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controlador de autenticación.
 * Gestiona el acceso de usuarios mediante login y el registro de nuevas cuentas de cliente.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Patrón para validar email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Procesa la autenticación del usuario y devuelve un token JWT si las credenciales son válidas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Validar formato de email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "error", "El correo es requerido"));
            }

            if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("success", false, "error", "El formato del correo no es válido"));
            }

            // Validar contraseña
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "error", "La contraseña es requerida"));
            }

            // Verificar si el usuario existe
            User user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Usuario no encontrado"));
            }

            // Intentar autenticación
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole().getName()
            );

            LoginResponse loginData = new LoginResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().getName(),
                    token
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Inicio de sesión exitoso",
                    "data", loginData
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Contraseña incorrecta"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Credenciales incorrectas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    /**
     * Registra un nuevo usuario en el sistema con el rol predeterminado de cliente.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        try {
            // Validar formato de email
            if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("success", false, "error", "El formato del correo no es válido"));
            }

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "error", "El correo ya está registrado"));
            }

            Role clientRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setPhone(request.getPhone());
            user.setRole(clientRole);

            userRepository.save(user);

            String token = jwtService.generateToken(
                    user.getEmail(),
                    clientRole.getName()
            );

            LoginResponse loginData = new LoginResponse(
                    user.getId(),
                    user.getEmail(),
                    clientRole.getName(),
                    token
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Usuario registrado exitosamente",
                            "data", loginData
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}
