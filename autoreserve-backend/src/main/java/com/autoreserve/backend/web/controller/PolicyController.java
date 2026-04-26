package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Policy;
import com.autoreserve.backend.domain.repository.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRepository policyRepository;

    public PolicyController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    /** Obtener todas las políticas (público) */
    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyRepository.findAll());
    }

    /** Obtener política por slug (público) */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getPolicyBySlug(@PathVariable String slug) {
        return policyRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Actualizar política (solo ADMIN) */
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePolicy(@PathVariable String slug,
                                          @RequestBody Map<String, String> body) {
        try {
            Policy policy = policyRepository.findBySlug(slug).orElseGet(() -> {
                Policy p = new Policy();
                p.setSlug(slug);
                p.setTitle(body.getOrDefault("title", slug));
                return p;
            });
            if (body.containsKey("title")) policy.setTitle(body.get("title"));
            if (body.containsKey("content")) policy.setContent(body.get("content"));
            policy.setUpdatedAt(LocalDateTime.now());
            policyRepository.save(policy);
            return ResponseEntity.ok(Map.of("success", true, "message", "Política actualizada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
