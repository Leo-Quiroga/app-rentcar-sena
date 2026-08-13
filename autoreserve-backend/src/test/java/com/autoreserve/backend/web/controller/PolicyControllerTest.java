package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Policy;
import com.autoreserve.backend.domain.repository.PolicyRepository;
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
class PolicyControllerTest {

    @Mock
    private PolicyRepository policyRepository;

    private PolicyController controller;
    private Policy policy;

    @BeforeEach
    void setUp() {
        controller = new PolicyController(policyRepository);
        policy = new Policy();
        policy.setId(1L);
        policy.setSlug("policy-1");
        policy.setTitle("Política de cancelación");
        policy.setContent("Política de cancelación completa.");
        policy.setSortOrder(1);
        policy.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllPolicies_ReturnsPolicyList() {
        when(policyRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(policy));

        ResponseEntity<List<Policy>> response = controller.getAllPolicies();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(policy);
    }

    @Test
    void createPolicy_ReturnsSuccess() {
        when(policyRepository.findMaxSortOrder()).thenReturn(1);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> {
            Policy saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ResponseEntity<?> response = controller.createPolicy(Map.of("title", "Términos", "content", "Contenido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("id")).isEqualTo(2L);
    }

    @Test
    void updatePolicy_ReturnsSuccess() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        ResponseEntity<?> response = controller.updatePolicy(1L, Map.of("title", "Términos actualizados"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(policy.getTitle()).isEqualTo("Términos actualizados");
    }

    @Test
    void updatePolicy_OnlyContent_ReturnsSuccess() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        ResponseEntity<?> response = controller.updatePolicy(1L, Map.of("content", "Contenido actualizado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(policy.getContent()).isEqualTo("Contenido actualizado");
    }

    @Test
    void createPolicy_DefaultValues_ReturnsSuccess() {
        when(policyRepository.findMaxSortOrder()).thenReturn(1);
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> {
            Policy saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        ResponseEntity<?> response = controller.createPolicy(Map.of());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("id")).isEqualTo(3L);
    }

    @Test
    void updatePolicy_NotFound_ReturnsError() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updatePolicy(999L, Map.of("title", "Términos"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Política no encontrada");
    }

    @Test
    void deletePolicy_ReordersRemainingPolicies() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(policy));

        ResponseEntity<?> response = controller.deletePolicy(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("message")).isEqualTo("Política eliminada y numeración actualizada");
    }

    @Test
    void deletePolicy_NotFound_ReturnsError() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.deletePolicy(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Política no encontrada");
    }
}
