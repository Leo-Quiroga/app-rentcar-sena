package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.dto.auth.LoginRequest;
import com.autoreserve.backend.dto.auth.LoginResponse;
import com.autoreserve.backend.domain.entity.User;
import com.autoreserve.backend.domain.repository.UserRepository;
import com.autoreserve.backend.security.jwt.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1. Autenticación (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Cargar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generar JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );

        // 4. Respuesta
        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                token
        );

        return ResponseEntity.ok(response);
    }
}
