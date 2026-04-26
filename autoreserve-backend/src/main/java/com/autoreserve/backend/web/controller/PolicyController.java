package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Policy;
import com.autoreserve.backend.domain.repository.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRepository policyRepository;

    public PolicyController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    /** Obtener todas las políticas ordenadas (público) */
    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyRepository.findAllByOrderBySortOrderAsc());
    }

    /** Crear nueva política (solo ADMIN) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPolicy(@RequestBody Map<String, String> body) {
        try {
            Integer maxOrder = policyRepository.findMaxSortOrder();
            Policy policy = new Policy();
            policy.setSlug("policy-" + System.currentTimeMillis());
            policy.setTitle(body.getOrDefault("title", "Nueva política"));
            policy.setContent(body.getOrDefault("content", ""));
            policy.setSortOrder(maxOrder + 1);
            policy.setUpdatedAt(LocalDateTime.now());
            Policy saved = policyRepository.save(policy);
            return ResponseEntity.ok(Map.of("success", true, "message", "Política creada", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Actualizar política (solo ADMIN) */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePolicy(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        try {
            Policy policy = policyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Política no encontrada"));
            if (body.containsKey("title")) policy.setTitle(body.get("title"));
            if (body.containsKey("content")) policy.setContent(body.get("content"));
            policy.setUpdatedAt(LocalDateTime.now());
            policyRepository.save(policy);
            return ResponseEntity.ok(Map.of("success", true, "message", "Política actualizada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Eliminar política y reordenar automáticamente (solo ADMIN) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id) {
        try {
            policyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Política no encontrada"));
            policyRepository.deleteById(id);

            // Reordenar las políticas restantes
            List<Policy> remaining = policyRepository.findAllByOrderBySortOrderAsc();
            AtomicInteger order = new AtomicInteger(1);
            remaining.forEach(p -> p.setSortOrder(order.getAndIncrement()));
            policyRepository.saveAll(remaining);

            return ResponseEntity.ok(Map.of("success", true, "message", "Política eliminada y numeración actualizada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
