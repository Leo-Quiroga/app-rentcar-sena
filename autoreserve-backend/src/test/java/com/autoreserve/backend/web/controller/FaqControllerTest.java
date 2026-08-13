package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Faq;
import com.autoreserve.backend.domain.repository.FaqRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqControllerTest {

    @Mock
    private FaqRepository faqRepository;

    private FaqController controller;
    private Faq faq;

    @BeforeEach
    void setUp() {
        controller = new FaqController(faqRepository);
        faq = new Faq();
        faq.setId(1L);
        faq.setQuestion("¿Cómo reservo?");
        faq.setAnswer("Puedes reservar en línea.");
        faq.setSortOrder(1);
        faq.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllFaqs_ReturnsFaqList() {
        when(faqRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(faq));

        ResponseEntity<List<Faq>> response = controller.getAllFaqs();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(faq);
    }

    @Test
    void createFaq_ReturnsSuccess() {
        when(faqRepository.findMaxSortOrder()).thenReturn(1);
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> {
            Faq saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ResponseEntity<?> response = controller.createFaq(Map.of("question", "Nueva pregunta", "answer", "Nueva respuesta"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("id")).isEqualTo(2L);
    }

    @Test
    void updateFaq_ReturnsSuccess() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        ResponseEntity<?> response = controller.updateFaq(1L, Map.of("question", "¿Cómo pago?", "answer", "Con tarjeta"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faq.getQuestion()).isEqualTo("¿Cómo pago?");
    }

    @Test
    void updateFaq_OnlyQuestion_ReturnsSuccess() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        ResponseEntity<?> response = controller.updateFaq(1L, Map.of("question", "¿Cómo pago?"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faq.getAnswer()).isEqualTo("Puedes reservar en línea.");
        assertThat(faq.getQuestion()).isEqualTo("¿Cómo pago?");
    }

    @Test
    void updateFaq_OnlyAnswer_ReturnsSuccess() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        ResponseEntity<?> response = controller.updateFaq(1L, Map.of("answer", "En efectivo"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faq.getQuestion()).isEqualTo("¿Cómo reservo?");
        assertThat(faq.getAnswer()).isEqualTo("En efectivo");
    }

    @Test
    void updateFaq_NotFound_ReturnsError() {
        when(faqRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updateFaq(999L, Map.of("question", "¿Cómo pago?"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("FAQ no encontrada");
    }

    @Test
    void deleteFaq_ReordersRemainingFaqs() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));
        when(faqRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(faq));

        ResponseEntity<?> response = controller.deleteFaq(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("message")).isEqualTo("FAQ eliminada y numeración actualizada");
    }

    @Test
    void deleteFaq_NotFound_ReturnsError() {
        when(faqRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.deleteFaq(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("FAQ no encontrada");
    }
}
