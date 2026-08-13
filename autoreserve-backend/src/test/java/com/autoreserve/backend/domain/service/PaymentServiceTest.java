package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Payment;
import com.autoreserve.backend.domain.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void save_ReturnsPersistedPayment() {
        Payment payment = new Payment();
        payment.setAmount(new java.math.BigDecimal("100.00"));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.save(payment);

        assertThat(result.getAmount()).isEqualByComparingTo("100.00");
        verify(paymentRepository).save(payment);
    }

    @Test
    void findAll_ReturnsAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment(), new Payment()));

        List<Payment> result = paymentService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_ExistingId_ReturnsPayment() {
        Payment payment = new Payment();
        payment.setAmount(new java.math.BigDecimal("50.00"));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.findById(999L);

        assertThat(result).isEmpty();
    }
}
