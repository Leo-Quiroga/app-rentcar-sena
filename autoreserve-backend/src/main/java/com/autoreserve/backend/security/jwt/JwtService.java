package com.autoreserve.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio encargado de la gestión de tokens JWT.
 * Realiza la generación, firmado y extracción de información de los tokens de acceso.
 */
@Service
public class JwtService {

    private static final String SECRET = "autoreserve_super_secret_key_which_is_long_enough";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hora

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Genera un nuevo token JWT para un usuario autenticado.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Analiza el token y extrae todos sus claims (peticiones de datos).
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae específicamente el correo electrónico (subject) contenido en el token.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
}