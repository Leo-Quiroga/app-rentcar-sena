package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Faq;
import com.autoreserve.backend.domain.repository.FaqRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/faq")
public class FaqController {

    private final FaqRepository faqRepository;

    public FaqController(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    /** Obtener todas las FAQ ordenadas (público) */
    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqRepository.findAllByOrderBySortOrderAsc());
    }

    /** Crear nueva FAQ (solo ADMIN) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFaq(@RequestBody Map<String, String> body) {
        try {
            Integer maxOrder = faqRepository.findMaxSortOrder();
            Faq faq = new Faq();
            faq.setQuestion(body.getOrDefault("question", "Nueva pregunta"));
            faq.setAnswer(body.getOrDefault("answer", ""));
            faq.setSortOrder(maxOrder + 1);
            faq.setUpdatedAt(LocalDateTime.now());
            Faq saved = faqRepository.save(faq);
            return ResponseEntity.ok(Map.of("success", true, "message", "FAQ creada", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Actualizar FAQ (solo ADMIN) */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFaq(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        try {
            Faq faq = faqRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("FAQ no encontrada"));
            if (body.containsKey("question")) faq.setQuestion(body.get("question"));
            if (body.containsKey("answer")) faq.setAnswer(body.get("answer"));
            faq.setUpdatedAt(LocalDateTime.now());
            faqRepository.save(faq);
            return ResponseEntity.ok(Map.of("success", true, "message", "FAQ actualizada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Eliminar FAQ y reordenar automáticamente (solo ADMIN) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFaq(@PathVariable Long id) {
        try {
            faqRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("FAQ no encontrada"));
            faqRepository.deleteById(id);
            List<Faq> remaining = faqRepository.findAllByOrderBySortOrderAsc();
            AtomicInteger order = new AtomicInteger(1);
            remaining.forEach(f -> f.setSortOrder(order.getAndIncrement()));
            faqRepository.saveAll(remaining);
            return ResponseEntity.ok(Map.of("success", true, "message", "FAQ eliminada y numeración actualizada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
